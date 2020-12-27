package com.georgiyshur.heartask.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.model.TestData
import com.georgiyshur.heartask.model.usecase.GetArtistsUseCase
import com.georgiyshur.heartask.viewmodel.ArtistsViewModel
import com.georgiyshur.heartask.viewmodel.DataState
import com.jraska.livedata.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Tests for [ArtistsViewModel].
 */
class ArtistsViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var underTest: ArtistsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should load artists data on success`() = runBlockingTest {
        underTest = ArtistsViewModel(GetArtistsUseCaseFake())

        val testObserver = underTest.artistsLiveData.test()

        testObserver.assertValueHistory(
            DataState.Loading,
            DataState.Loaded(TestData.ARTISTS)
        )
    }

    @Test
    fun `Should throw error on data error`() = runBlockingTest {
        val error = GetArtistsUseCaseFake.Error()
        underTest = ArtistsViewModel(GetArtistsUseCaseFake(error))

        val testObserver = underTest.artistsLiveData.test()

        testObserver.assertValueHistory(
            DataState.Loading,
            DataState.Error(error)
        )
    }

    private class GetArtistsUseCaseFake(private val error: Throwable? = null) : GetArtistsUseCase {

        override suspend fun invoke(): List<Artist> {
            if (error == null) {
                return TestData.ARTISTS
            } else {
                throw error
            }
        }

        class Error : Exception()
    }
}