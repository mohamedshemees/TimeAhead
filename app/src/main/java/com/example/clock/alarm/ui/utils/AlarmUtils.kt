package com.example.clock.alarm.ui.utils

import com.example.clock.alarm.domain.Alarm
import java.util.Calendar
import java.util.TimeZone
import java.util.TimeZone.getTimeZone

object AlarmUtils {
    fun getNextValidTime(alarm: Alarm): Long {
        val repeatDays = alarm.getRepeatDays()
        val calendar = Calendar.getInstance(getTimeZone("UTC")).apply {
            timeInMillis = alarm.timeInMillis
        }
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L

        var nextTimeInMillis = alarm.timeInMillis
        if (nextTimeInMillis <= currentTime) {
            if (repeatDays.isEmpty()) {
                while (nextTimeInMillis <= currentTime) {
                    nextTimeInMillis += oneDayInMillis
                }
            } else {
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
                val nextDayIndex = repeatDays.indexOfFirst { it > currentDayOfWeek }
                    .takeIf { it >= 0 } ?: repeatDays.first()
                val daysToAdd = (nextDayIndex - currentDayOfWeek + 7) % 7
                    .let { if (it == 0 && nextTimeInMillis <= currentTime) 7 else it }
                nextTimeInMillis += daysToAdd * oneDayInMillis
                while (nextTimeInMillis <= currentTime) {
                    nextTimeInMillis += 7 * oneDayInMillis
                }
            }
        }
        return nextTimeInMillis
    }
}