package com.georgiyshur.heartask.model

/**
 * Represents the state of the song currently playing.
 */
sealed class PlayerState {

    object Idle : PlayerState()
    data class Active(
        val song: Song,
        val isPlaying: Boolean,
        val progress: Float
    ) : PlayerState()
}