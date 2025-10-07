package com.example.clock.worldClock

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.ClockApp
import com.example.clock.utils.getCurrentTime
import com.example.clock.worldClock.data.WorldClockRepository
import com.example.clock.worldClock.ui.TimeZoneItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs


class WorldClockViewModel(var repository: WorldClockRepository,
                          application: ClockApp
) : AndroidViewModel(application) {

    private var _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime


    private var _timeZoneList = MutableLiveData<List<TimeZoneItem.TimeZone>>()
    var timeZoneList :LiveData<List<TimeZoneItem.TimeZone>> = _timeZoneList

    init {
        getTimeZoneList()
        startTimer()
        updateList()
    }
    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                _currentTime.postValue(getCurrentTime())
                delay(1000)
            }
        }

    }
    fun updateList()=viewModelScope.launch {
        while (true) {
            val updatedList = _timeZoneList.value?.map { item ->
                val zoneId = ZoneId.of(item.label)
                val currentTime = ZonedDateTime.now(zoneId)
                    .format(DateTimeFormatter.ofPattern("HH:mm"))

                val difference=getLocalTimeDifference(item.label)

                item.copy(currentTime = currentTime,
                    offset = difference
                )
            }
            if (!updatedList.isNullOrEmpty()) {
                _timeZoneList.value= updatedList
            }
            delay(60000)
        }
    }
    fun getLocalTimeDifference(zone: String): String {
        val localZone = ZoneId.systemDefault()  // Local timezone
        val targetZone = ZoneId.of(zone)        // Target timezone

        val now = Instant.now() // Current UTC time (same moment globally)
        val localTime = now.atZone(localZone)
        val targetTime = now.atZone(targetZone)

        val duration = Duration.between(localTime.toLocalTime(), targetTime.toLocalTime())

        val totalMinutes = duration.toMinutes()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 || minutes > 0 -> "$hours hours $minutes minutes ahead"
            hours < 0 || minutes < 0 -> "${abs(hours)} hours ${abs(minutes)} minutes behind"
            else -> "Same as local time"
        }
    }

    private fun getTimeZoneList() {
        repository.getAll().observeForever { timeZones ->
            _timeZoneList.postValue(timeZones)
        }
    }

    fun inserTimezone(timezone: TimeZoneItem.TimeZone)=viewModelScope.launch(Dispatchers.IO) {
        repository.insert(timezone)
    }

     fun deleteTimzone(selectedTimeZones: List<TimeZoneItem.TimeZone>) =viewModelScope.launch(Dispatchers.IO) {
        repository.delete(selectedTimeZones)
    }
}
