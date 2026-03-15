package com.example.clock.alarm.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clock.RingtoneProvider
import com.example.clock.alarm.data.AlarmRepository

class AlarmEditingViewModelFactory(
    private val application: Application,
    private val repository: AlarmRepository,
    private val ringtoneProvider: RingtoneProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmEditingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmEditingViewModel(application, repository, ringtoneProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
