package com.example.clock.worldClock.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.clock.worldClock.ui.TimeZoneItem


@Dao
interface WorldClockDao {

@Query("SELECT * FROM TimeZone")
   fun getAll(): LiveData<List<TimeZoneItem.TimeZone>>

@Insert
suspend fun insert(timZone: TimeZoneItem.TimeZone)

    @Delete
   suspend  fun delete(selectedTimeZones: List<TimeZoneItem.TimeZone>)


}