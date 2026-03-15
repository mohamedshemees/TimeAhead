package com.example.clock.worldClock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clock.alarm.data.AlarmConverters
import com.example.clock.alarm.data.dao.AlarmDao
import com.example.clock.alarm.data.entities.AlarmEntity
import com.example.clock.timer.data.TimerPresetDao
import com.example.clock.timer.domain.TimerPreset
import com.example.clock.worldClock.ui.TimeZoneItem

@Database(entities = [AlarmEntity::class, TimeZoneItem.TimeZone::class, TimerPreset::class], version = 1, exportSchema = false)
@TypeConverters(AlarmConverters::class)
abstract class ClockDataBase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
    abstract fun worldClockDao(): WorldClockDao
    abstract fun timerDao(): TimerPresetDao

    companion object {
        @Volatile
        private var INSTANCE: ClockDataBase? = null

        fun getDatabase(context: Context): ClockDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClockDataBase::class.java,
                    "Clock_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}