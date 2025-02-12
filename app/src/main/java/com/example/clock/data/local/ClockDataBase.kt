package com.example.clock.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.clock.data.model.Alarm

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
@TypeConverters(AlarmConverters::class)
abstract class ClockDataBase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

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