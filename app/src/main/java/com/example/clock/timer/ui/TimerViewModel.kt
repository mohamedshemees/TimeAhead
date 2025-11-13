package com.example.clock.timer.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.ClockApp
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.broadcastreceiver.AlarmReceiver
import com.example.clock.timer.data.TimerRepository
import com.example.clock.timer.domain.TimerPreset
import kotlinx.coroutines.launch

class TimerViewModel(
    var repository: TimerRepository,
    application: ClockApp
    ) : AndroidViewModel(application) {

    var allPresets: LiveData<List<TimerPreset>>
        init {
            allPresets = repository.getAllAlarms()
        }
    suspend fun insert(preset: TimerPreset) = viewModelScope.launch {
        repository.insert(preset)
    }

    fun update(preset: TimerPreset) = viewModelScope.launch {
        repository.update(preset)
    }

    fun delete(preset: TimerPreset) = viewModelScope.launch {
        repository.delete(preset)
    }

    fun getAllPresets() {
        allPresets = repository.getAllAlarms()
    }

    fun deletePresets(presets: List<TimerPreset>) = viewModelScope.launch {
        for (alarm in presets) {
            //cancelAlarm(application, alarm)
        }
        repository.deleteAlarms(presets)
    }




//    fun insertOrUpdateAlarm(updatedAlarm: TimerPreset, oldAlarm: TimerPreset)=viewModelScope.launch  {
//        val existingAlarm = oldAlarm
//
////        if (existingAlarm != null) {
////            // If the primary key has changed, delete the old entry first
////            if (existingAlarm.label != updatedAlarm.label ||
////                existingAlarm.timeInMillis != updatedAlarm.timeInMillis ||
////                existingAlarm.days != updatedAlarm.days) {
////
////                repository.delete(existingAlarm) // Remove old alarm with old primary key
////            }
////        }
//
//        // Insert the updated alarm (whether it's a new one or a replacement)
//        repository.insert(updatedAlarm)
    }



