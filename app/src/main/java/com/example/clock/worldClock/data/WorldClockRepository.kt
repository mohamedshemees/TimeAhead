package com.example.clock.worldClock.data

import androidx.lifecycle.LiveData
import com.example.clock.worldClock.ui.TimeZoneItem

class WorldClockRepository (
      private var worldClockDao: WorldClockDao
){

      fun getAll(): LiveData<List<TimeZoneItem.TimeZone>> {
       return worldClockDao.getAll()
    }

     suspend fun insert(timZone: TimeZoneItem.TimeZone) {
        worldClockDao.insert(timZone)
    }

    suspend fun delete(selectedTimeZones: List<TimeZoneItem.TimeZone>) {
        worldClockDao.delete(selectedTimeZones)
    }
}