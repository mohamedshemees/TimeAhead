package com.example.clock.stopwatch

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clock.stopwatch.ui.Lap
import kotlinx.coroutines.launch

class StopWatchViewModel : ViewModel() {
    private val _counter = MutableLiveData<Long>()
    val counter: LiveData<Long> = _counter

    private val _lapcounter = MutableLiveData<Long>()

    private var _counterformated = MutableLiveData<String>()
    val counterformatted: LiveData<String> = _counterformated

    private var _lapformatted = MutableLiveData<String>()
    val lapformatted: LiveData<String> = _lapformatted

    private val _laps = MutableLiveData<List<Lap>>()
    val laps: LiveData<List<Lap>> = _laps

    private val  _counterOn = MutableLiveData(false)
    val counterOn: LiveData<Boolean> = _counterOn

    private val _addingLap = MutableLiveData(false)

    private val handler = Handler(Looper.getMainLooper())


    private var startTime: Long = 0
    private var lapStartTime: Long = 0


    fun toggleCounter() {
        if (_counterOn.value == true) {
            handler.removeCallbacks(updateRunnable)
        } else {
            if ((_counter.value ?: 0) > 0) {
                val pauseDuration = System.currentTimeMillis() - startTime - (_counter.value ?: 0)
                startTime += pauseDuration
                lapStartTime += pauseDuration
            } else {
                startTime = System.currentTimeMillis() - (_counter.value ?: 0)
                lapStartTime = System.currentTimeMillis() - (_lapcounter.value ?: 0)
            }
            handler.post(updateRunnable)
        }
        _counterOn.value = !(_counterOn.value ?: false)
    }


    private val updateRunnable = object : Runnable {
        override fun run() {
            if (_counterOn.value == true) {

                val currentTime = System.currentTimeMillis()
                _counter.value = currentTime - startTime
                _lapcounter.value = currentTime - lapStartTime
                _counterformated.value = formatTime(_counter.value ?: 0)
                _lapformatted.value = formatTime(_lapcounter.value ?: 0)
                handler.postDelayed(this, 16)
            }
        }
    }


    fun addlap() = viewModelScope.launch{
        _addingLap.postValue(true)
        val lapTime = _lapcounter.value ?: 0
        val overallTime = _counter.value ?: 0
        val newLap = Lap(
            (_laps.value?.size ?: 0) + 1,
            formatTime(lapTime),
            formatTime(overallTime)
        )
        //delay(10)
        Thread.sleep(100)
        val updatedLaps = _laps.value.orEmpty().toMutableList()
        updatedLaps.add(newLap)

        _laps.postValue(updatedLaps)

        _lapcounter.postValue(0)
        lapStartTime = System.currentTimeMillis()

        _lapformatted.postValue("00:00.00")
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
        _counterformated.value = "00:00.00"
        _counterOn.postValue(false)
        handler.removeCallbacks(updateRunnable)
    }
}


