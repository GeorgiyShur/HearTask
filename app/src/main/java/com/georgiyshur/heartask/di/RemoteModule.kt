package com.georgiyshur.heartask.di

import com.georgiyshur.heartask.BuildConfig
import com.georgiyshur.heartask.model.api.ApiDescription
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RemoteModule {

    @Provides
    @Singleton
    fun provideApiDescription(): ApiDescription {
        val client = OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api-v2.hearthis.at")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create()
    }
}