package com.example.clock.alarm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.clock.alarm.data.entities.AlarmEntity

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarms(alarms: List<AlarmEntity>)

    @Query("SELECT * FROM alarm_table WHERE alarmId = :id")
    suspend fun getAlarmById(id: Int): AlarmEntity?

    @Query("SELECT * FROM alarm_table ORDER BY timeInMillis ASC")
    fun getAllAlarms(): LiveData<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_table ORDER BY timeInMillis ASC")
    fun getAllAlarmsSynchronous(): List<AlarmEntity>
}
