package com.example.clock.alarm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clock.ClockApp
import com.example.clock.alarm.data.AlarmRepository

class AlarmViewModelFactory(private val repository: AlarmRepository,
                            private val application: ClockApp)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}