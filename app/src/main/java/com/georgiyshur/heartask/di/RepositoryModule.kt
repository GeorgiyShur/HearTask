package com.georgiyshur.heartask.di

import com.georgiyshur.heartask.model.api.ApiDescription
import com.georgiyshur.heartask.model.repository.FeedRepository
import com.georgiyshur.heartask.model.repository.FeedRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideFeedRepository(apiDescription: ApiDescription): FeedRepository {
        return FeedRepositoryImpl(apiDescription)
    }
}