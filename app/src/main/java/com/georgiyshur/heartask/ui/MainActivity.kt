package com.georgiyshur.heartask.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.georgiyshur.heartask.model.service.AudioPlaybackService
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
import com.georgiyshur.heartask.viewmodel.PlayerViewModel
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
    private val playerViewModel by viewModels<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO bind service on play and unbind on stop (atm stop isn't introduced)
        bindToAudioPlaybackService()

        setContent {
            MainScreen(
                artistSongsViewModelFactory = artistSongsViewModelFactory
            )
        }
    }

    private fun bindToAudioPlaybackService() {
        AudioPlaybackService.newIntent(this).also { intent ->
            bindService(intent, playerViewModel.connection, Context.BIND_AUTO_CREATE)
        }
    }
}