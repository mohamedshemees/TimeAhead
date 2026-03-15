package com.example.clock.alarm.data.mappers

import com.example.clock.alarm.data.entities.AlarmEntity
import com.example.clock.alarm.domain.Alarm

fun AlarmEntity.toDomain(): Alarm {
    return Alarm(
        alarmId = alarmId,
        timeInMillis = timeInMillis,
        label = label,
        days = days,
        amPm = amPm,
        sound = Alarm.AlarmSound(
            soundOn = soundOn,
            soundName = soundName,
            soundUri = soundUri
        ),
        vibrate = Alarm.AlarmVibration(
            vibrationOn = vibrationOn,
            behavior = vibrationBehavior
        ),
        snooze = Alarm.AlarmSnooze(
            snoozeOn = snoozeOn,
            test = snoozeInterval
        ),
        isEnabled = isEnabled
    )
}

fun Alarm.toEntity(): AlarmEntity {
    return AlarmEntity(
        alarmId = alarmId,
        timeInMillis = timeInMillis,
        label = label,
        days = days,
        amPm = amPm,
        soundOn = sound.soundOn,
        soundName = sound.soundName,
        soundUri = sound.soundUri,
        vibrationOn = vibrate.vibrationOn,
        vibrationBehavior = vibrate.behavior,
        snoozeOn = snooze.snoozeOn,
        snoozeInterval = snooze.test,
        isEnabled = isEnabled
    )
}
