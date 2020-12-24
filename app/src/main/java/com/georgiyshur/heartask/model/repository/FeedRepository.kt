package com.georgiyshur.heartask.model.repository

import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.model.api.ApiDescription

/**
 * Repository which provides feed with songs and artists.
 */
interface FeedRepository {

    /**
     * List of all songs available.
     */
    suspend fun getFeed(): List<Song>
}

class FeedRepositoryImpl(private val apiDescription: ApiDescription) : FeedRepository {

    private var feed: List<Song>? = null // In-memory cached feed

    override suspend fun getFeed(): List<Song> {
        return feed ?: apiDescription.feed().also { feed = it } // Cache the feed if not cached
    }
}