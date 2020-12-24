package com.georgiyshur.heartask.model.usecase

import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.model.repository.FeedRepository

/**
 * Provides the list all available artists.
 */
interface GetArtistsUseCase {

    suspend operator fun invoke(): List<Artist>
}

class GetArtistsUseCaseImpl(private val feedRepository: FeedRepository) : GetArtistsUseCase {

    override suspend operator fun invoke(): List<Artist> {
        return feedRepository.getFeed().map { it.artist }.distinct().sortedBy { it.username }
    }
}