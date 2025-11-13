package com.example.clock.timer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clock.ClockApp
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.timer.data.TimerRepository

class TimerViewModelFactory(private val repository: TimerRepository,
                            private val application: ClockApp)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(repository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}