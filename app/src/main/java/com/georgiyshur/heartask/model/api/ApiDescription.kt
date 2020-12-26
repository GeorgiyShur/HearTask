package com.georgiyshur.heartask.model.api

import com.georgiyshur.heartask.model.Song
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Description of HearThisAt REST API service.
 */
interface ApiDescription {

    @GET("feed/")
    suspend fun feed(
        @Query("page") page: Int = 1,
        @Query("count") count: Int = 30
    ): List<Song>
}