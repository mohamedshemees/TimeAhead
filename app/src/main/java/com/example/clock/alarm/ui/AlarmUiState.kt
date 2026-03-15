package com.example.clock.alarm.ui

data class AlarmUiState(
    val alarmId: Int = -1,
    val timeInMillis: Long = System.currentTimeMillis(),
    val label: String = "",
    val days: String = "",
    val amPm: String = "am",
    val sound: AlarmSoundUiState = AlarmSoundUiState(),
    val vibrate: AlarmVibrationUiState = AlarmVibrationUiState(),
    val snooze: AlarmSnoozeUiState = AlarmSnoozeUiState(),
    val isEnabled: Boolean = true
)

data class AlarmSoundUiState(
    val soundOn: Boolean = true,
    val soundName: String = "Default",
    val soundUri: String = ""
)

data class AlarmVibrationUiState(
    val vibrationOn: Boolean = true,
    val behavior: String = "Standard"
)

data class AlarmSnoozeUiState(
    val snoozeOn: Boolean = true,
    val test: String = "5 minutes"
)
