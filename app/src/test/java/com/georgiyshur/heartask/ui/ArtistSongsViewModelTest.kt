package com.georgiyshur.heartask.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.georgiyshur.heartask.model.Artist
import com.georgiyshur.heartask.model.Song
import com.georgiyshur.heartask.model.TestData
import com.georgiyshur.heartask.model.usecase.GetArtistByIdUseCase
import com.georgiyshur.heartask.model.usecase.GetSongsForArtistUseCase
import com.georgiyshur.heartask.viewmodel.ArtistSongsViewModel
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
 * Tests for [ArtistSongsViewModel].
 */
class ArtistSongsViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var underTest: ArtistSongsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        underTest = ArtistSongsViewModel(
            getArtistByIdUseCase = GetArtistByIdUseCaseFake(),
            getSongsForArtistUseCase = GetSongsForArtistUseCaseFake(),
            artistId = "0"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should load artist data`() = runBlockingTest {
        val testObserver = underTest.artistLiveData.test()

        testObserver.assertValueHistory(
            TestData.createArtist()
        )
    }

    @Test
    fun `Should load songs of particular artist`() = runBlockingTest {
        val testObserver = underTest.songsLiveData.test()

        testObserver.assertValueHistory(
            DataState.Loading,
            DataState.Loaded(listOf(TestData.createSong(0), TestData.createSong(1)))
        )
    }

    @Test
    fun `Should throw error on songs data error`() = runBlockingTest {
        val error = GetSongsForArtistUseCaseFake.Error()
        underTest = ArtistSongsViewModel(
            getArtistByIdUseCase = GetArtistByIdUseCaseFake(),
            getSongsForArtistUseCase = GetSongsForArtistUseCaseFake(error),
            artistId = "0"
        )

        val testObserver = underTest.songsLiveData.test()

        testObserver.assertValueHistory(
            DataState.Loading,
            DataState.Error(error)
        )
    }

    private class GetArtistByIdUseCaseFake : GetArtistByIdUseCase {

        override suspend fun invoke(artistId: String): Artist {
            return TestData.createArtist()
        }
    }

    private class GetSongsForArtistUseCaseFake(
        private val error: Throwable? = null
    ) : GetSongsForArtistUseCase {

        override suspend fun invoke(artistId: String): List<Song> {
            if (error == null) {
                return listOf(TestData.createSong(0), TestData.createSong(1))
            } else {
                throw error
            }
        }

        class Error : Exception()
    }
}