package com.example.clock.worldClock.ui

import androidx.room.Entity
import androidx.room.PrimaryKey

sealed class TimeZoneItem {
    data class Header(val text: String) : TimeZoneItem()
    @Entity
    data class TimeZone(
        @PrimaryKey
        val label: String,
        val offset: String,
        val currentTime: String
    ): TimeZoneItem()

    data class TimeZoneGroup(val timeZones: List<TimeZone>) : TimeZoneItem()

}
