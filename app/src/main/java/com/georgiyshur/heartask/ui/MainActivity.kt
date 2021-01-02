package com.georgiyshur.heartask.ui

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.georgiyshur.heartask.model.PlaybackListener
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.model.service.AudioPlaybackService
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    /*
    It seems that there is no way of injecting the view-model with arguments into composables
    using Hilt. This is a temporary solution inspired by official sample app Crane. Should be
    refactored when better Hilt-Compose interoperability is introduced.
     */
    @Inject
    lateinit var artistSongsViewModelFactory: ArtistSongsViewModel.AssistedFactory

    private var audioPlaybackService: AudioPlaybackService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlaybackService.AudioServiceBinder
            audioPlaybackService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioPlaybackService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                artistSongsViewModelFactory = artistSongsViewModelFactory,
                object : PlaybackListener {
                    override fun play(song: Song) {
                        audioPlaybackService?.playSong(song)
                    }

                    override fun pause() {
                        audioPlaybackService?.pause()
                    }

                    override fun stop() {
                        audioPlaybackService?.stop()
                    }
                }
            )
        }

        bindToAudioPlaybackService()
    }

    private fun bindToAudioPlaybackService() {
        if (audioPlaybackService == null) {
            AudioPlaybackService.newIntent(this).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }
}