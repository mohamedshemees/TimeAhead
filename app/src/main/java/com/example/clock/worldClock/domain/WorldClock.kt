package com.example.clock.worldClock.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorldClock(

    @PrimaryKey
    val timezone: String,
    val offset: String,
)
