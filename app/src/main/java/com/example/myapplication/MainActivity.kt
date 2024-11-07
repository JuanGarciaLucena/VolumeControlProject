package com.example.myapplication

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private lateinit var volumeControlContainer: LinearLayout
    private lateinit var volumeBars: List<View>
    private lateinit var buttonSetLines: Button
    private lateinit var buttonSetVolume: Button
    private lateinit var editTextSetLines: EditText
    private lateinit var editTextSetVolume: EditText
    private lateinit var gestureDetector: GestureDetector

    private val viewModel: VolumeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        buttonSetLines = findViewById(R.id.buttonSetLines)
        buttonSetVolume = findViewById(R.id.buttonSetVolume)
        editTextSetLines = findViewById(R.id.editTextSetLines)
        editTextSetVolume = findViewById(R.id.editTextSetVolume)
        volumeControlContainer = findViewById(R.id.volumeControlContainer)
        gestureDetector = GestureDetector(this, VolumeGestureListener())

        volumeBars = listOf(
            findViewById(R.id.line1),
            findViewById(R.id.line2),
            findViewById(R.id.line3),
            findViewById(R.id.line4),
            findViewById(R.id.line5),
            findViewById(R.id.line6),
            findViewById(R.id.line7),
            findViewById(R.id.line8),
            findViewById(R.id.line9),
            findViewById(R.id.line10)
        )

        viewModel.volumeLevel.observe(this) { level ->
            updateVolumeBars(level)
            editTextSetLines.setText(level.toString())
        }

        viewModel.volumePercentage.observe(this) { percentage ->
            editTextSetVolume.setText(percentage.toString())
        }


        buttonSetLines.setOnClickListener {
            val level = editTextSetLines.text.toString().toIntOrNull()
            if (level != null && level in 0..10) {
                viewModel.setVolumeByLevel(level)
            } else {
                editTextSetLines.error = "Enter a number between 0 and 10"
            }
        }

        buttonSetVolume.setOnClickListener {
            val percentage = editTextSetVolume.text.toString().toIntOrNull()
            if (percentage != null && percentage in 0..100) {
                viewModel.setVolumeByPercentage(percentage)
            } else {
                editTextSetVolume.error = "Enter a percentage between 0 and 100"
            }
        }

        volumeControlContainer.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun updateVolumeBars(level: Int) {
        for (i in volumeBars.indices) {
            volumeBars[i].setBackgroundColor(
                if (i < level) 0xFF6A89CC.toInt() else 0xFFBBBBBB.toInt()
            )
        }
    }

    inner class VolumeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val velocityThreshold = 500
        private val distanceThreshold = 50

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 != null) {
                val distanceY = e2.y - e1.y
                val absVelocityY = abs(velocityY)

                if (absVelocityY > velocityThreshold && abs(distanceY) > distanceThreshold) {
                    if (velocityY < 0) {
                        viewModel.adjustVolume(1)
                    } else {
                        viewModel.adjustVolume(-1)
                    }
                    return true
                }
            }
            return false
        }
    }
}