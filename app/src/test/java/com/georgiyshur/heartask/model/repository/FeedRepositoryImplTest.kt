package com.georgiyshur.heartask.model.repository

import com.georgiyshur.heartask.model.TestData
import com.georgiyshur.heartask.model.api.ApiDescription
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Tests for [FeedRepositoryImpl].
 */
class FeedRepositoryImplTest {

    private val apiDescription = mockk<ApiDescription> {
        coEvery { feed() } returns TestData.FEED
    }

    private lateinit var underTest: FeedRepositoryImpl

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        underTest = FeedRepositoryImpl(
            apiDescription = apiDescription
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should provide correct data from API`() = runBlocking {
        val feed = underTest.getFeed()

        assertEquals(TestData.FEED, feed)
    }

    @Test
    fun `Should not call API again when data is cached`() = runBlocking {
        underTest.getFeed()
        underTest.getFeed()

        coVerify(exactly = 1) {
            apiDescription.feed()
        }
    }
}