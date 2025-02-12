package com.example.clock.data.model


import android.os.Parcelable
import androidx.room.Entity
import com.example.clock.ClockApp
import com.example.clock.data.repository.AlarmRepository
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.TimeZone



@Parcelize
@Serializable
@Entity(tableName = "alarm_table", primaryKeys = ["timeInMillis", "label", "days"])
data class Alarm(
    var timeInMillis: Long =0L ,
    var label: String = "",
    var days: String = "Monday",
    var am_pm: String = "am",
    var sound: AlarmSound = AlarmSound(),
    var vibrate: AlarmVibration = AlarmVibration(),
    var snooze: AlarmSnooze = AlarmSnooze(),
    var Enabled: Boolean = true
) : Parcelable {

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


}
fun getDefaultTimeMillis(): Long {
    val calendar = Calendar.getInstance().apply {
        timeZone = TimeZone.getDefault() // Ensures it uses the local time zone
        set(Calendar.HOUR_OF_DAY, 6) // Set hour to 6 AM
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}
