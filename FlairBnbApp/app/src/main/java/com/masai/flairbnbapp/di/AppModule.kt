package com.masai.flairbnbapp.di

import com.masai.flairbnbapp.repository.FlairRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun providesRepository(): FlairRepository {
        return FlairRepository()
    }

}