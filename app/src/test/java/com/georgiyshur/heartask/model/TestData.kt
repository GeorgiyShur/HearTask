package com.georgiyshur.heartask.model

/**
 * Helper object for testing data.
 */
object TestData {

    val FEED = listOf(
        createSong(0),
        createSong(1),
        createSong(2),
        createSong(3),
        createSong(4),
    )

    fun createArtist(id: Int = 0): Artist {
        return Artist(
            id = id.toString(),
            username = "Username $id",
            avatarUrl = "http://avatar$id.jpg"
        )
    }

    fun createSong(id: Int = 0): Song {
        return Song(
            id = id.toString(),
            title = "Title $id",
            duration = id * 1000L,
            streamUrl = "http://stream$id",
            artist = createArtist(id = id.rem(3)),
            genre = "Genre $id",
            artworkUrl = "http://artwork$id.jpg",
            waveformUrl = "http://waveform$id.jpg"
        )
    }
}