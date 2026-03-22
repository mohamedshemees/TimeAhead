package com.example.clock.alarm.ui.mappers

import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.*

fun Alarm.toUiState(): AlarmUiState {
    return AlarmUiState(
        alarmId = alarmId,
        timeInMillis = timeInMillis,
        label = label,
        days = days,
        amPm = amPm,
        sound = AlarmSoundUiState(
            soundOn = sound.soundOn,
            soundName = sound.soundName,
            soundUri = sound.soundUri
        ),
        vibrate = AlarmVibrationUiState(
            vibrationOn = vibrate.vibrationOn,
            behavior = vibrate.behavior
        ),
        snooze = AlarmSnoozeUiState(
            snoozeOn = snooze.snoozeOn,
            test = snooze.test
        ),
        isEnabled = isEnabled
    )
}

fun AlarmUiState.toDomain(): Alarm {
    return Alarm(
        alarmId = alarmId,
        timeInMillis = timeInMillis,
        label = label,
        days = days,
        amPm = amPm,
        sound = Alarm.AlarmSound(
            soundOn = sound.soundOn,
            soundName = sound.soundName,
            soundUri = sound.soundUri
        ),
        vibrate = Alarm.AlarmVibration(
            vibrationOn = vibrate.vibrationOn,
            behavior = vibrate.behavior
        ),
        snooze = Alarm.AlarmSnooze(
            snoozeOn = snooze.snoozeOn,
            test = snooze.test
        ),
        isEnabled = isEnabled
    )
}
