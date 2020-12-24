package com.georgiyshur.heartask.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Model class for song entity.
 *
 * Note: normally I'd use separate models for API, domain and presentation, but for the sake of
 * simplicity only one will be used for all layers in this project.
 */
@JsonClass(generateAdapter = true)
data class Song(
    val id: String,
    val title: String,
    val duration: Long,
    @Json(name = "stream_url") val streamUrl: String,
    @Json(name = "user") val artist: Artist,
    val genre: String?,
    @Json(name = "artwork_url") val artworkUrl: String?,
    @Json(name = "waveform_url") val waveformUrl: String?,
)