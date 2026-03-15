package com.example.clock.alarm.domain

data class Alarm(
    val alarmId: Int,
    val timeInMillis: Long,
    val label: String,
    val days: String,
    val amPm: String,
    val sound: AlarmSound,
    val vibrate: AlarmVibration,
    val snooze: AlarmSnooze,
    val isEnabled: Boolean
) {
    data class AlarmSound(
        val soundOn: Boolean,
        val soundName: String,
        val soundUri: String
    )

    data class AlarmVibration(
        val vibrationOn: Boolean,
        val behavior: String
    )

    data class AlarmSnooze(
        val snoozeOn: Boolean,
        val test: String
    )

    fun getRepeatDays(): List<Int> {
        if (days.isEmpty()) return emptyList()

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val cleanDays = days.removePrefix("every ").trim()

        return cleanDays.split(",").mapNotNull { day ->
            val index = day.trim().let { dayNames.indexOf(it) }
            index.takeIf { it >= 0 }
        }
    }
}
