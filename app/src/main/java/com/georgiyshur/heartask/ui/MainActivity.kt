package com.georgiyshur.heartask.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen(
                artistSongsViewModelFactory = artistSongsViewModelFactory
            )
        }
    }
}