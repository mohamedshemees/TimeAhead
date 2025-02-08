package com.example.clock.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table",primaryKeys = ["time","label","days"])
data class Alarm(
    val time: String,
    val label: String,
    var days: String,
    var am_pm: String,
    @Embedded var sound:AlarmSound,
    @Embedded var vibrate: AlarmVibration,
    var Enabled: Boolean,
) {
    data class AlarmSound(
        var soundOn: Boolean,
        var soundName: String
    )

    class AlarmVibration  (
        var vibrationOn: Boolean,
        var Behavior: String
    )
}
