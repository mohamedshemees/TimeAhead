package com.example.clock.alarm.ui

interface AlarmEditingInteractionListener {
    fun initAlarm(alarmId: Int)
    fun updateTime(hour: Int, minute: Int)
    fun toggleDay(dayIndex: Int)
    fun updateLabel(label: String)
    fun updateRingtone(ringtone: Ringtone)
    fun updateSoundOn(checked: Boolean)
    fun updateVibrateOn(checked: Boolean)
    fun updateSnoozeOn(checked: Boolean)
    fun saveAlarm()
    fun cancel()
}
