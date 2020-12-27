package com.georgiyshur.heartask.model.usecase

import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.model.repository.FeedRepository

/**
 * Provides artist detail by its ID.
 */
interface GetArtistByIdUseCase {

    suspend operator fun invoke(artistId: String): Artist
}

class GetArtistByIdUseCaseImpl(
    private val feedRepository: FeedRepository
) : GetArtistByIdUseCase {

    override suspend operator fun invoke(artistId: String): Artist {
        return feedRepository.getFeed().find { it.artist.id == artistId }?.artist
            ?: throw ArtistNotFoundExeption()
    }
}

class ArtistNotFoundExeption : Exception()