package com.example.clock.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.clock.data.model.Alarm

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarms(alarms: List<Alarm>)


    @Query("SELECT * FROM alarm_table WHERE label = :label AND timeInMillis = :time AND days = :days")
        suspend fun getAlarm(label: String, time: String, days: String): Alarm?


    @Query("SELECT * FROM alarm_table ORDER BY timeInMillis ASC")

    fun getAllAlarms(): LiveData<List<Alarm>>

    @Query("SELECT * FROM alarm_table ORDER BY timeInMillis ASC")
    fun getAllAlarmsSynchronous(): List<Alarm>
}