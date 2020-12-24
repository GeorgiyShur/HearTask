package com.georgiyshur.heartask.model.usecase

import com.georgiyshur.heartask.model.TestData
import com.georgiyshur.heartask.model.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for [GetArtistsUseCaseImpl].
 */
class GetArtistsUseCaseImplTest {

    private val feedRepository = object : FeedRepository {
        override suspend fun getFeed() = TestData.FEED
    }

    private lateinit var underTest: GetArtistsUseCaseImpl

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        underTest = GetArtistsUseCaseImpl(
            feedRepository = feedRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should provide correct artists from feed`() = runBlockingTest {
        val artists = underTest()

        assertEquals(
            listOf(
                TestData.createArtist(0),
                TestData.createArtist(1),
                TestData.createArtist(2),
            ),
            artists
        )
    }
}