package com.georgiyshur.heartask.viewmodel

/**
 * Class representing the state of data to be presented.
 */
sealed class DataState<out T> {

    object Loading : DataState<Nothing>()
    data class Loaded<T>(val data: T) : DataState<T>()
    data class Error(val error: Throwable?) : DataState<Nothing>()
}
