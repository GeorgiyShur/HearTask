package com.georgiyshur.heartask.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Model class for artist entity.
 *
 * Note: normally I'd use separate models for API, domain and presentation, but for the sake of
 * simplicity only one will be used for all layers in this project.
 */
@JsonClass(generateAdapter = true)
data class Artist(
    val id: String,
    val username: String,
    @Json(name = "avatar_url") val avatarUrl: String?
)