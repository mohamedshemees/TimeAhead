package com.example.clock.timer.domain

import androidx.room.Entity

@Entity(tableName = "Timer_table", primaryKeys = ["name","hours","minutes","seconds"])
data class TimerPreset(
    var name: String,
    var hours: Int,
    var minutes: Int,
    var seconds: Int
)