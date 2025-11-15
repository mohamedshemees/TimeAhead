package com.example.clock.alarm.ui

import com.example.clock.alarm.domain.Alarm

interface AlarmEditingInteractionListener {
    fun initAlarm(alarm: Alarm)
    fun updateTime(hour: Int, minute: Int)
    fun toggleDay(dayIndex: Int)
    fun updateLabel(label: String)
    fun updateRingtone(ringtone: SoundPickerFragment.Ringtone)
    fun updateSoundOn(checked: Boolean)
    fun updateVibrateOn(checked: Boolean)
    fun updateSnoozeOn(checked: Boolean)
    fun saveAlarm()
    fun cancel()
}