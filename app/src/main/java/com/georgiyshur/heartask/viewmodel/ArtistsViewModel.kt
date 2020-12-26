package com.georgiyshur.heartask.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.georgiyshur.heartask.model.usecase.GetArtistsUseCase

/**
 * ViewModel for the main screen.
 */
class ArtistsViewModel @ViewModelInject constructor(
    private val getArtistsUseCase: GetArtistsUseCase
) : ViewModel() {

    val artistsLiveData = liveData {
        emit(DataState.Loading)
        try {
            emit(DataState.Loaded(getArtistsUseCase()))
        } catch (throwable: Throwable) {
            emit(DataState.Error(throwable))
        }
    }
}