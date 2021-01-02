package com.georgiyshur.heartask.model

/**
 * Interface for playback controls.
 */
interface PlaybackListener {

    fun play(song: Song)

    fun pause()

    fun stop()
}