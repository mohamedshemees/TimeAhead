package com.example.clock.utils
import com.example.clock.worldClock.ui.TimeZoneItem
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale


fun getFlattenedTimeZoneList(query: String = ""): List<TimeZoneItem> {
    return ZoneId.getAvailableZoneIds()
        .sorted()
        .groupBy { it.first().uppercaseChar().toString() }
        .flatMap { (letter, zones) ->
            val filteredZones = zones.filter { it.contains(query, ignoreCase = true) }


            if (filteredZones.isNotEmpty()) {
                listOf(TimeZoneItem.Header(letter)) +
                        listOf(TimeZoneItem.TimeZoneGroup(filteredZones.map { it.toTimeZoneItem() }))
            } else {
                emptyList()
            }
        }
}

private fun String.toTimeZoneItem(): TimeZoneItem.TimeZone {
    val zoneId = ZoneId.of(this)
    val offset = ZonedDateTime.now(zoneId).offset
    return TimeZoneItem.TimeZone(this, "GMT$offset", getZoneTime())
}

fun getCurrentTime(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("h:mm:ss a", Locale.getDefault())
    return formatter.format(calendar.time)
}
fun getZoneTime(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
    return formatter.format(calendar.time)
}


