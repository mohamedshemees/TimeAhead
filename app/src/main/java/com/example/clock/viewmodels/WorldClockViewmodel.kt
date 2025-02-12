package com.example.clock.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clock.data.repository.WorldClockRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class WorldClockViewmodel : ViewModel(){

    private var _currentTime = MutableLiveData<String>()
    val currentTime: MutableLiveData<String> = _currentTime


    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                _currentTime.postValue(getCurrentTime()) // Update time
                delay(1000) // Wait for 1 second
            }
        }
    }
    private fun getCurrentTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val formatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
        return formatter.format(calendar.time)
    }

}