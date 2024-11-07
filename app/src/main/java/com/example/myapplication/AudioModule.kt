package com.example.myapplication

import android.app.Application
import android.content.Context
import android.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AudioModule {
    @Provides
    @Singleton
    fun provideAudioManager(application: Application): AudioManager {
        return application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
}