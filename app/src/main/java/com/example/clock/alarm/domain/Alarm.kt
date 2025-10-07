package com.example.clock.alarm.domain


import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Entity(tableName = "alarm_table", primaryKeys = ["timeInMillis", "label", "days"])
data class Alarm(
    var alarmId: Int = 0,
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
        if(days.contains("every")) {
            days.removeRange(0..4)
        }
        return days.split(", ").mapNotNull { day -> dayNames.indexOf(day.trim()).takeIf { it >= 0 } }
    }
}

