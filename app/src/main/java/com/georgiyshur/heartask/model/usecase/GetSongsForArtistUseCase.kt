package com.georgiyshur.heartask.model.usecase

import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.model.repository.FeedRepository

/**
 * Provides the list all songs by one particular artist.
 */
interface GetSongsForArtistUseCase {

    suspend operator fun invoke(artistId: String): List<Song>
}

class GetSongsForArtistUseCaseImpl(
    private val feedRepository: FeedRepository
) : GetSongsForArtistUseCase {

    override suspend operator fun invoke(artistId: String): List<Song> {
        return feedRepository.getFeed().filter { it.artist.id == artistId }.sortedBy { it.title }
    }
}