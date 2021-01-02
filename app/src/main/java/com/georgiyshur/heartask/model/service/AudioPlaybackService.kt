package com.georgiyshur.heartask.model.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LifecycleService
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.georgiyshur.heartask.R
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.ui.MainActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import timber.log.Timber

/**
 * Service responsible for playing the audio on background. It handles media session and sticky
 * notification.
 */
class AudioPlaybackService : LifecycleService() {

    companion object {

        private const val CHANNEL_ID = "playback_channel"
        private const val NOTIFICATION_ID = 1
        private const val MEDIA_SESSION_TAG = "sed_audio"

        fun newIntent(context: Context) = Intent(context, AudioPlaybackService::class.java)
    }

    inner class AudioServiceBinder : Binder() {

        val service
            get() = this@AudioPlaybackService
    }

    private lateinit var player: SimpleExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var currentSong: Song? = null
    private var artworkBitmap: Bitmap? = null
    private var artworkDisposable: Disposable? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return AudioServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        initPlayer()
        initNotificationManager()
        initMediaSession()
        initMediaSessionConnector()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
    }

    fun playSong(song: Song) {
        if (song != currentSong) {
            currentSong = song
            prepare(song.streamUrl)
            song.artworkUrl?.let { loadArtwork(it) }
        }
        player.playWhenReady = true
    }

    fun pause() {
        player.playWhenReady = false
    }

    fun stop() {
        artworkDisposable?.dispose()
        playerNotificationManager.setPlayer(null)
        player.apply {
            playWhenReady = false
            release()
        }
        mediaSession.apply {
            isActive = false
            release()
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(baseContext).build()
        player.audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        player.addListener(PlayerEventListener())
    }

    private fun initNotificationManager() {
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            CHANNEL_ID,
            R.string.audio_playback,
            R.string.audio_playback_description,
            NOTIFICATION_ID,
            MediaDescriptionAdapter(),
            NotificationListener()
        ).apply {
            // Omit skip previous and next actions.
            setUsePreviousAction(false)
            setUseNextAction(false)

            // Add stop action.
            setUseStopAction(true)

            setPlayer(player)
        }
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(applicationContext, MEDIA_SESSION_TAG).apply {
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
        val loader = ImageLoader(this)
        val request = ImageRequest.Builder(applicationContext)
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
                applicationContext,
                0,
                Intent(applicationContext, MainActivity::class.java),
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

    private inner class NotificationListener : PlayerNotificationManager.NotificationListener {

        override fun onNotificationStarted(notificationId: Int, notification: Notification) {
            startForeground(notificationId, notification)
        }

        override fun onNotificationCancelled(notificationId: Int) {
            stopSelf()
        }

        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {
            if (ongoing) {
                // Make sure the service will not get destroyed while playing media.
                startForeground(notificationId, notification)
            } else {
                // Make notification cancellable.
                stopForeground(false)
            }
        }
    }

    private inner class PlayerEventListener : Player.EventListener {

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Timber.d(playbackState.toString())
        }

        override fun onPlayerError(e: ExoPlaybackException) {
            Timber.e(e)
        }
    }
}