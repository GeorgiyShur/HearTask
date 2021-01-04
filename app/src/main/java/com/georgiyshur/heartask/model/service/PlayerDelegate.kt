package com.georgiyshur.heartask.model.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.georgiyshur.heartask.R
import com.georgiyshur.heartask.model.PlayerState
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.ui.MainActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager

/**
 * Delegate that handles all the player logic (player itself, media session, notification).
 */
class PlayerDelegate(
    private val context: Context,
    private val notificationListener: AudioPlaybackService.NotificationListener
) {

    companion object {

        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
        private const val MEDIA_SESSION_TAG = "sed_audio"
    }

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var currentSong: Song? = null
    private var artworkBitmap: Bitmap? = null
    private var artworkDisposable: Disposable? = null

    private val _playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Idle)
    val playerStateLiveData: LiveData<PlayerState> = _playerStateLiveData

    private val eventListener = EventListener()
    private val progressUpdateHandler = Handler(Looper.getMainLooper())
    private val progressUpdateRunnable = object : Runnable {
        override fun run() {
            eventListener.updateState()
            progressUpdateHandler.postDelayed(this, 1000L) // Poll the position each second
        }
    }

    init {
        initPlayer()
        initNotificationManager()
        initMediaSession()
        initMediaSessionConnector()
    }

    fun play(song: Song) {
        if (song != currentSong) {
            player.playWhenReady = false
            currentSong = song
            prepare(song.streamUrl)
            song.artworkUrl?.let { loadArtwork(it) }
        }
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun rewind() {
        player.seekTo(player.currentPosition - 10000)
    }

    fun forward() {
        player.seekTo(player.currentPosition + 10000)
    }

    fun stop() {
        progressUpdateHandler.removeCallbacks(progressUpdateRunnable)
        artworkDisposable?.dispose()
        playerNotificationManager.setPlayer(null)
        player.apply {
            playWhenReady = false
            removeListener(eventListener)
            release()
        }
        mediaSession.apply {
            isActive = false
            release()
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(context).build()
        player.audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        player.addListener(eventListener)
    }

    private fun initNotificationManager() {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            context,
            CHANNEL_ID,
            R.string.audio_playback,
            R.string.audio_playback_description,
            NOTIFICATION_ID,
            MediaDescriptionAdapter(),
            notificationListener
        ).apply {
            // Omit stop, skip previous and next actions
            setUseStopAction(false)
            setUsePreviousAction(false)
            setUseNextAction(false)

            setPlayer(player)
        }
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(context, MEDIA_SESSION_TAG).apply {
            // Set the session's token so that client activities can communicate with it
            isActive = true
            playerNotificationManager.setMediaSessionToken(sessionToken)
        }
    }

    private fun initMediaSessionConnector() {
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    val bitmap = artworkBitmap
                    val extras = Bundle().apply {
                        putParcelable(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                        putParcelable(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, bitmap)
                    }

                    val title = currentSong?.title ?: "Unknown title"

                    return MediaDescriptionCompat.Builder()
                        .setIconBitmap(bitmap)
                        .setTitle(title)
                        .setExtras(extras)
                        .build()
                }
            })

            setPlayer(player)
        }
    }

    private fun loadArtwork(url: String) {
        artworkDisposable?.dispose()
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(url)
            .target { drawable ->
                artworkBitmap = (drawable as BitmapDrawable).bitmap
                mediaSessionConnector.invalidateMediaSessionQueue()
                mediaSessionConnector.invalidateMediaSessionMetadata()
            }
            .build()
        artworkDisposable = loader.enqueue(request)
    }

    private fun prepare(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    private inner class MediaDescriptionAdapter :
        PlayerNotificationManager.MediaDescriptionAdapter {

        override fun getCurrentContentTitle(player: Player): String {
            return currentSong?.title ?: "Unknown title"
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        override fun getCurrentContentText(player: Player): String? {
            return null
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            return artworkBitmap
        }
    }

    private inner class EventListener : Player.EventListener {

        override fun onPlaybackStateChanged(state: Int) {
            updateState()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            updateState()
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                progressUpdateHandler.post(progressUpdateRunnable)
            } else {
                progressUpdateHandler.removeCallbacks(progressUpdateRunnable)
            }
        }

        fun updateState() {
            if (player.playbackState == Player.STATE_READY) {
                _playerStateLiveData.postValue(
                    PlayerState.Active(
                        song = currentSong!!, // Current song should not be null in this state
                        isPlaying = player.playWhenReady,
                        progress = player.contentPosition.toFloat() / player.contentDuration
                    )
                )
            }
            if (player.playbackState == Player.STATE_ENDED) {
                _playerStateLiveData.postValue(PlayerState.Idle)
            }
        }
    }
}