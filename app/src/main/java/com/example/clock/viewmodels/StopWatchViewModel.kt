package com.example.clock.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.clock.ui.stopwatch.Lap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StopWatchViewModel : ViewModel() {
    private val _counter = MutableLiveData<Long>()
    val counter: LiveData<Long> = _counter

    private val _lapcounter = MutableLiveData<Long>()
    val lapcounter: LiveData<Long> = _lapcounter

    private var _counterforammted = MutableLiveData<String>()
    val counterformatted: LiveData<String> = _counterforammted

    private var _lapforamtted = MutableLiveData<String>()
    val lapformatted: LiveData<String> = _lapforamtted

    private val _laps = MutableLiveData<List<Lap>>()
    val laps: LiveData<List<Lap>> = _laps

    private val _counterOn = MutableLiveData(false)
    val counterOn: LiveData<Boolean> = _counterOn

    private val _addingLap = MutableLiveData(false)


    fun toggleCounter() {
        _counterOn.value = !(_counterOn.value ?: false)
        if (_counterOn.value == true) {
            updatetime()
        }

    }

    private fun updatetime() = viewModelScope.launch {
        while (_counterOn.value == true) {
            if(_addingLap.value==true){
                delay(500)
            }
            delay(10) // Update every 10ms
            _counter.postValue((_counter.value ?: 0) + 10)
            _lapcounter.postValue((_lapcounter.value ?: 0) + 10)
            _counterforammted.postValue(formatTime(_counter.value ?: 0))
            _lapforamtted.postValue(formatTime(_lapcounter.value ?: 0))
        }

    }

    fun addlap() = viewModelScope.launch (Dispatchers.Main){

        _addingLap.postValue(true)
        val lapTime = _lapcounter.value ?: 0
        val overallTime = _counter.value ?: 0

        val newLap = Lap(
            (_laps.value?.size ?: 0) + 1,
            formatTime(lapTime),
            formatTime(overallTime)
        )
        delay(50)
        val updatedLaps = _laps.value.orEmpty().toMutableList()
        updatedLaps.add(newLap)
        _laps.postValue(updatedLaps)

        _lapcounter.postValue(0)
        _lapforamtted.postValue("00:00.00")

        _addingLap.postValue(false)

    }

    private fun formatTime(ms: Long): String {
        val minutes = (ms / 60000) % 60
        val seconds = (ms / 1000) % 60
        val millis = (ms % 1000) / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, millis)
    }

    fun resetCounter() {
        _laps.value = emptyList()
        _counter.value = 0
        _counterforammted.value = "00:00.00"
        _counterOn.postValue(false)
    }
}


