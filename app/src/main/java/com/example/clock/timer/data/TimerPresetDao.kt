package com.example.clock.timer.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.clock.timer.domain.TimerPreset

@Dao
interface TimerPresetDao {


    @Insert
    suspend fun insertPreset(preset: TimerPreset)

    @Delete
    suspend fun deletePreset(presetName: TimerPreset)

    @Update
    suspend fun updatePreset(preset: TimerPreset)

    @Delete
    suspend fun deletePresets(alarms: List<TimerPreset>)

    @Query("Select * FROM Timer_table ")
     fun getAllPresets(): LiveData<List<TimerPreset>>

}