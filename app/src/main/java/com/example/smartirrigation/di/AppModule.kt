package com.example.smartirrigation.di

import android.content.Context
import com.example.smartirrigation.repository.CropRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideCropRepository(
        @ApplicationContext context: Context
    ): CropRepository {
        return CropRepository(context)
    }
}
