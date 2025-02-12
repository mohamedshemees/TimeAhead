package com.example.clock.data.local

import androidx.room.TypeConverter
import com.example.clock.data.model.Alarm
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AlarmConverters {

    @TypeConverter
    fun fromAlarmSound(alarmSound: Alarm.AlarmSound): String {
        return Json.encodeToString( alarmSound)
    }

    @TypeConverter
    fun toAlarmSound(data: String): Alarm.AlarmSound {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromAlarmVibration(alarmVibration: Alarm.AlarmVibration): String {
        return Json.encodeToString(alarmVibration)
    }

    @TypeConverter
    fun toAlarmVibration(data: String): Alarm.AlarmVibration {
        return Json.decodeFromString(data)
    }

    @TypeConverter
    fun fromAlarmSnooze(alarmSnooze: Alarm.AlarmSnooze): String {
        return Json.encodeToString(alarmSnooze)
    }

    @TypeConverter
    fun toAlarmSnooze(data: String): Alarm.AlarmSnooze {
        return Json.decodeFromString(data)
    }
}
