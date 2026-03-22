package com.example.clock.alarm.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
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
import com.example.clock.alarm.ui.mappers.toDomain
import com.example.clock.alarm.ui.mappers.toUiState
import com.example.clock.RingtoneProvider
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AlarmEditingUiState(
    val alarm: AlarmUiState = AlarmUiState(),
    val selectedDaysText: String = "",
    val ringtones: List<Ringtone> = emptyList(),
    val finishActivity: Boolean = false
)

class AlarmEditingViewModel(
    application: Application,
    private val repository: AlarmRepository,
    private val ringtoneProvider: RingtoneProvider
) : AndroidViewModel(application), AlarmEditingInteractionListener {

    private val _uiState = MutableLiveData<AlarmEditingUiState>()
    val uiState: LiveData<AlarmEditingUiState> = _uiState

    private val daysList = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val selectedDays = mutableSetOf<Int>()
    private var timeInMillis: Long = 0L
    private var isToday: Boolean = false

    init {
        _uiState.value = AlarmEditingUiState()
        loadRingtones()
    }

    private fun loadRingtones() {
        viewModelScope.launch {
            val ringtones = ringtoneProvider.getSystemRingtones()
            _uiState.value = _uiState.value?.copy(ringtones = ringtones)
        }
    }

    override fun initAlarm(alarmId: Int) {
        viewModelScope.launch {
            val alarm = if (alarmId != -1) {
                repository.getAlarmById(alarmId)
            } else {
                createDefaultAlarm()
            }

            alarm?.let {
                _uiState.value = _uiState.value?.copy(alarm = it.toUiState())
                timeInMillis = it.timeInMillis
                selectedDays.clear()
                selectedDays.addAll(it.getRepeatDays())
                updateSelectedDaysText()
            }
        }
    }

    private fun createDefaultAlarm(): Alarm {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
        return Alarm(
            alarmId = -1,
            timeInMillis = calendar.timeInMillis,
            label = "",
            days = "",
            amPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "am" else "pm",
            sound = Alarm.AlarmSound(true, "Default", defaultSoundUri),
            vibrate = Alarm.AlarmVibration(true, "Standard"),
            snooze = Alarm.AlarmSnooze(true, "5 minutes"),
            isEnabled = true
        )
    }

    override fun updateTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        timeInMillis = calendar.timeInMillis
        isToday = !calendar.before(Calendar.getInstance())
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
        _uiState.value = _uiState.value?.let { state ->
            state.copy(alarm = state.alarm.copy(label = label))
        }
    }

    override fun updateRingtone(ringtone: Ringtone) {
        _uiState.value = _uiState.value?.let { state ->
            val newSound = state.alarm.sound.copy(soundName = ringtone.title, soundUri = ringtone.uri.toString())
            state.copy(alarm = state.alarm.copy(sound = newSound))
        }
    }

    override fun updateSoundOn(checked: Boolean) {
        _uiState.value = _uiState.value?.let { state ->
            val newSound = state.alarm.sound.copy(soundOn = checked)
            state.copy(alarm = state.alarm.copy(sound = newSound))
        }
    }

    override fun updateVibrateOn(checked: Boolean) {
        _uiState.value = _uiState.value?.let { state ->
            val newVibration = state.alarm.vibrate.copy(vibrationOn = checked)
            state.copy(alarm = state.alarm.copy(vibrate = newVibration))
        }
    }

    override fun updateSnoozeOn(checked: Boolean) {
        _uiState.value = _uiState.value?.let { state ->
            val newSnooze = state.alarm.snooze.copy(snoozeOn = checked)
            state.copy(alarm = state.alarm.copy(snooze = newSnooze))
        }
    }

    override fun saveAlarm() {
        Log.d("ALARM", "Saving alarm")
        viewModelScope.launch {
            val state = _uiState.value ?: return@launch
            val updatedAlarmUi = state.alarm.copy(
                timeInMillis = timeInMillis,
                days = state.selectedDaysText,
                isEnabled = true
            )
            val domainAlarm = updatedAlarmUi.toDomain()

            if (domainAlarm.alarmId != -1) {
                repository.update(domainAlarm)
                setAlarm(getApplication(), domainAlarm)
            } else {
                val newId = repository.insert(domainAlarm)
                val alarmWithId = domainAlarm.copy(alarmId = newId.toInt())
                setAlarm(getApplication(), alarmWithId)
            }

            _uiState.postValue(state.copy(finishActivity = true))
        }
    }

    override fun cancel() {
        _uiState.value = _uiState.value?.copy(finishActivity = true)
    }

    private fun updateSelectedDaysText() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = this@AlarmEditingViewModel.timeInMillis
        }
        val shortDayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)

        val text = when {
            selectedDays.isEmpty() -> {
                val now = Calendar.getInstance()
                if (calendar.after(now)) {
                    "Today-$shortDayName,$dateNumber"
                } else {
                    val tomorrow = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, 1) }
                    val tomorrowName = SimpleDateFormat("EEE", Locale.getDefault()).format(tomorrow.time)
                    val tomorrowNumber = tomorrow.get(Calendar.DAY_OF_MONTH)
                    "Tomorrow-$tomorrowName,$tomorrowNumber"
                }
            }
            selectedDays.size == 7 -> "Every day"
            else -> "every ${selectedDays.sorted().joinToString(", ") { daysList[it] }}"
        }
        _uiState.value = _uiState.value?.copy(selectedDaysText = text)
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
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
}
