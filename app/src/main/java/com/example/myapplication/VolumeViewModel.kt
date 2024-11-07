package com.example.myapplication

import android.app.Application
import android.media.AudioManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VolumeViewModel @Inject constructor(
    application: Application,
    private val audioManager: AudioManager
): ViewModel() {

    private val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    private val _volumeLevel = MutableLiveData<Int>()
    val volumeLevel: LiveData<Int> = _volumeLevel

    private val _volumePercentage = MutableLiveData<Int>()
    val volumePercentage: LiveData<Int> = _volumePercentage

    init {
        updateVolumeData()
    }

    fun setVolumeByLevel(level: Int) {
        val deviceVolume = (level * maxVolume) / 10
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceVolume, AudioManager.FLAG_SHOW_UI)
        updateVolumeData()
    }

    fun setVolumeByPercentage(percentage: Int) {
        val deviceVolume = (percentage * maxVolume) / 100
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, deviceVolume, AudioManager.FLAG_SHOW_UI)
        updateVolumeData()
    }

    fun adjustVolume(delta: Int) {
        var currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        currentVolume = (currentVolume + delta).coerceIn(0, maxVolume)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI)
        updateVolumeData()
    }

    private fun updateVolumeData() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val level = (currentVolume * 10) / maxVolume
        val percentage = (currentVolume * 100) / maxVolume
        _volumeLevel.value = level
        _volumePercentage.value = percentage
    }

}