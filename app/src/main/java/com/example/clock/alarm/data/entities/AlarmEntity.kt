package com.example.clock.alarm.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val alarmId: Int = 0,
    val timeInMillis: Long,
    val label: String,
    val days: String,
    val amPm: String,
    val soundOn: Boolean,
    val soundName: String,
    val soundUri: String,
    val vibrationOn: Boolean,
    val vibrationBehavior: String,
    val snoozeOn: Boolean,
    val snoozeInterval: String,
    val isEnabled: Boolean
)
