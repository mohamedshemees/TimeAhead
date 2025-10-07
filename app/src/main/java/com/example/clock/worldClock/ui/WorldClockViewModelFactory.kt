package com.example.clock.worldClock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clock.ClockApp
import com.example.clock.worldClock.WorldClockViewModel
import com.example.clock.worldClock.data.WorldClockRepository

class WorldClockViewModelFactory(private var worldclockrepo: WorldClockRepository,
                                 private val app: ClockApp) :
    ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WorldClockViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WorldClockViewModel(worldclockrepo,app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}
