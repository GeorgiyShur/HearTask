package com.georgiyshur.heartask.di

import com.georgiyshur.heartask.model.repository.FeedRepository
import com.georgiyshur.heartask.model.usecase.GetArtistsUseCase
import com.georgiyshur.heartask.model.usecase.GetArtistsUseCaseImpl
import com.georgiyshur.heartask.model.usecase.GetSongsForArtistUseCase
import com.georgiyshur.heartask.model.usecase.GetSongsForArtistUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class UseCaseModule {

    @Provides
    @ActivityRetainedScoped
    fun provideGetArtistsUseCase(feedRepository: FeedRepository): GetArtistsUseCase {
        return GetArtistsUseCaseImpl(feedRepository)
    }

    @Provides
    @ActivityRetainedScoped
    fun provideGetSongsForArtistUseCase(feedRepository: FeedRepository): GetSongsForArtistUseCase {
        return GetSongsForArtistUseCaseImpl(feedRepository)
    }
}