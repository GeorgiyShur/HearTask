package com.georgiyshur.heartask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import com.georgiyshur.heartask.model.usecase.GetArtistByIdUseCase
import com.georgiyshur.heartask.model.usecase.GetSongsForArtistUseCase
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import timber.log.Timber

/**
 * ViewModel for the main screen.
 */
class ArtistSongsViewModel @AssistedInject constructor(
    private val getArtistByIdUseCase: GetArtistByIdUseCase,
    private val getSongsForArtistUseCase: GetSongsForArtistUseCase,
    @Assisted private val artistId: String
) : ViewModel() {

    val artistLiveData = liveData {
        try {
            emit(getArtistByIdUseCase(artistId))
        } catch (throwable: Throwable) {
            Timber.e(throwable)
        }
    }

    val songsLiveData = liveData {
        emit(DataState.Loading)
        try {
            emit(DataState.Loaded(getSongsForArtistUseCase(artistId)))
        } catch (throwable: Throwable) {
            emit(DataState.Error(throwable))
        }
    }

    @AssistedInject.Factory
    interface AssistedFactory {
        fun create(artistId: String): ArtistSongsViewModel
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            artistId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(artistId) as T
            }
        }
    }
}

@InstallIn(ActivityRetainedComponent::class)
@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectModule::class])
interface AssistedInjectModule