package com.example.clock.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.model.Alarm
import com.example.clock.data.repository.AlarmRepository
import kotlinx.coroutines.launch

class AlarmViewModel(
                     private var repository: AlarmRepository) : ViewModel() {

    val allAlarms: LiveData<List<Alarm>> = repository.allAlarms

    fun insert(alarm: Alarm) = viewModelScope.launch {
        repository.insert(alarm)
    }

    fun update(alarm: Alarm) = viewModelScope.launch {
        repository.update(alarm)
    }



    fun delete(alarm: Alarm) = viewModelScope.launch {
        repository.delete(alarm)
    }
}