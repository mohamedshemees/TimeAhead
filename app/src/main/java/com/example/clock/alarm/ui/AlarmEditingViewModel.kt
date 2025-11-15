package com.example.clock.alarm.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.alarm.data.AlarmRepository
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.broadcastreceiver.AlarmReceiver
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AlarmEditingUiState(
    val alarm: Alarm = Alarm(),
    val selectedDaysText: String = "",
    val finishActivity: Boolean = false
)

class AlarmEditingViewModel(
    application: Application,
    private val repository: AlarmRepository
) : AndroidViewModel(application), AlarmEditingInteractionListener {

    private val _uiState = MutableLiveData<AlarmEditingUiState>()
    val uiState: LiveData<AlarmEditingUiState> = _uiState

    private lateinit var originalAlarm: Alarm
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val selectedDays = mutableSetOf<Int>()
    private var timeInMillis: Long = 0L
    private var isToday: Boolean = false

    init {
        _uiState.value = AlarmEditingUiState()
    }

    override fun initAlarm(alarm: Alarm) {
        originalAlarm = alarm
        _uiState.value = _uiState.value?.copy(alarm = alarm)
        timeInMillis = alarm.timeInMillis
        selectedDays.addAll(alarm.getRepeatDays())
        updateSelectedDaysText()
    }

    override fun updateTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        timeInMillis = calendar.timeInMillis
        isToday = calendar.after(Calendar.getInstance())
        updateSelectedDaysText()
    }

    override fun toggleDay(dayIndex: Int) {
        if (selectedDays.contains(dayIndex)) {
            selectedDays.remove(dayIndex)
        } else {
            selectedDays.add(dayIndex)
        }
        updateSelectedDaysText()
    }

    override fun updateLabel(label: String) {
        val currentAlarm = _uiState.value?.alarm ?: return
        _uiState.value = _uiState.value?.copy(alarm = currentAlarm.copy(label = label))
    }

    override fun updateRingtone(ringtone: SoundPickerFragment.Ringtone) {
        val currentAlarm = _uiState.value?.alarm ?: return
        val newSound =
            currentAlarm.sound.copy(soundName = ringtone.title, soundUri = ringtone.uri.toString())
        _uiState.value = _uiState.value?.copy(alarm = currentAlarm.copy(sound = newSound))
    }

    override fun updateSoundOn(checked: Boolean) {
        val currentAlarm = _uiState.value?.alarm ?: return
        val newSound = currentAlarm.sound.copy(soundOn = checked)
        _uiState.value = _uiState.value?.copy(alarm = currentAlarm.copy(sound = newSound))
    }

    override fun updateVibrateOn(checked: Boolean) {
        val currentAlarm = _uiState.value?.alarm ?: return
        val newVibration = currentAlarm.vibrate.copy(vibrationOn = checked)
        _uiState.value = _uiState.value?.copy(alarm = currentAlarm.copy(vibrate = newVibration))
    }

    override fun updateSnoozeOn(checked: Boolean) {
        val currentAlarm = _uiState.value?.alarm ?: return
        val newSnooze = currentAlarm.snooze.copy(snoozeOn = checked)
        _uiState.value = _uiState.value?.copy(alarm = currentAlarm.copy(snooze = newSnooze))
    }

    override fun saveAlarm() {
        Log.d("ALARM", "Saving alarm")
        viewModelScope.launch {
            val currentAlarm = _uiState.value?.alarm ?: return@launch
            val updatedAlarm = currentAlarm.copy(
                timeInMillis = timeInMillis,
                days = _uiState.value?.selectedDaysText ?: "",
                Enabled = true
            )
            if (originalAlarm.alarmId == 0) {

                repository.update(updatedAlarm)
                cancelAlarm(getApplication(), originalAlarm)
            } else {
                repository.insert(updatedAlarm)
            }

            setAlarm(getApplication(), updatedAlarm)

            _uiState.postValue(_uiState.value?.copy(finishActivity = true))
        }
    }

    override fun cancel() {
        _uiState.value = _uiState.value?.copy(finishActivity = true)
    }

    private fun updateSelectedDaysText() {
        val calendar = Calendar.getInstance()
        val shortDayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)

        val text = when {
            selectedDays.isEmpty() -> if (isToday) "Today-$shortDayName,$dateNumber" else {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val tomorrowName =
                    SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                val tomorrowNumber = calendar.get(Calendar.DAY_OF_MONTH)
                "Tomorrow-$tomorrowName,$tomorrowNumber"
            }

            selectedDays.size == 7 -> "Every day"
            else -> "every ${selectedDays.sorted().joinToString(", ") { days[it] }}"
        }
        _uiState.value = _uiState.value?.copy(selectedDaysText = text)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.alarmId)
            putExtra("timeInMillis", alarm.timeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
            putExtra("soundOn", alarm.sound.soundOn)
            putExtra("soundUri", alarm.sound.soundUri)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMillis,
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}