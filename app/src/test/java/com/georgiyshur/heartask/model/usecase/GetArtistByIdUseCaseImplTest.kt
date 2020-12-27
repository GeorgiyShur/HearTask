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
 * Tests for [GetArtistByIdUseCaseImpl].
 */
class GetArtistByIdUseCaseImplTest {

    private val feedRepository = object : FeedRepository {
        override suspend fun getFeed() = TestData.FEED
    }

    private lateinit var underTest: GetArtistByIdUseCaseImpl

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        underTest = GetArtistByIdUseCaseImpl(
            feedRepository = feedRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should provide artist by ID`() = runBlockingTest {
        val artist = underTest("0")

        assertEquals(
            TestData.createArtist(0),
            artist
        )
    }
}