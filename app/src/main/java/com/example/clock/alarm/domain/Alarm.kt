package com.example.clock.alarm.domain


import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "alarm_table", primaryKeys = ["timeInMillis", "label", "days"])
data class Alarm(
    var alarmId: Int = -1,
    var timeInMillis: Long = 0,
    var label: String = "",
    var days: String = "Monday",
    var am_pm: String = "am",
    var sound: AlarmSound = AlarmSound(),
    var vibrate: AlarmVibration = AlarmVibration(),
    var snooze: AlarmSnooze = AlarmSnooze(),
    var Enabled: Boolean = true
) : Parcelable {
    init {
        if (alarmId == 0) {
            alarmId = "$timeInMillis$days$label".hashCode()
        }
    }

    @Serializable
    @Parcelize
    data class AlarmSound(
        var soundOn: Boolean = true,
        var soundName: String = "",
        var soundUri: String = ""
    ) : Parcelable


    @Serializable
    @Parcelize
    data class AlarmVibration(
        var vibrationOn: Boolean = true,
        var Behavior: String = ""
    ) : Parcelable


    @Serializable

    @Parcelize
    data class AlarmSnooze(
        var snoozeOn: Boolean = true,
        var test: String = "5 minutes"
    ) : Parcelable

    fun getRepeatDays(): List<Int> {
        if (days.isEmpty()) return emptyList()

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        val cleanDays = days.removePrefix("every ").trim()

        val result = cleanDays.split(",").mapNotNull { day ->
            val index = day.trim().let { dayNames.indexOf(it) }
            index.takeIf { it >= 0 }
        }
        return result
    }
}

