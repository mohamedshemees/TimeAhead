package com.example.clock.alarm.ui

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.clock.R
import com.example.clock.alarm.AlarmViewModel
import com.example.clock.alarm.domain.Alarm
import com.example.clock.alarm.ui.broadcastreceiver.AlarmReceiver
import com.example.clock.alarm.ui.utils.AlarmUtils.getNextValidTime
import com.example.clock.databinding.FragmentAlarmEditingBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AlarmEditingFragment : Fragment() {

    private var _binding: FragmentAlarmEditingBinding? = null
    private val binding get() = _binding!!
    private lateinit var soundSettingsContainer: LinearLayout
    private lateinit var tvSelectedDays: TextView
    private lateinit var oldAlarm: Alarm
    private lateinit var alarm: Alarm
    private var pickedRingtone: SoundPickerFragment.Ringtone? = null
    private val viewModel: AlarmViewModel by activityViewModels()
    private var timeInMillis: Long = 0L
    private var isToday: Boolean = false
    private var lastSelectedTime: Long = 0L
    private val selectedDays = mutableSetOf<Int>()
    private var isMultiSelectMode: Boolean = false
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmEditingBinding.inflate(inflater, container, false)
        soundSettingsContainer = binding.alarmsoundLl
        tvSelectedDays = binding.repetition

        setupAlarmData()
        setupViews()
        setupListeners()

        return binding.root
    }

    private fun setupAlarmData() {
        oldAlarm = arguments?.getParcelable("alarm") ?: Alarm()
        alarm = oldAlarm.copy()
        pickedRingtone = viewModel.repository.allRingtones[0]
        initializeTimeDefaults()
    }

    private fun initializeTimeDefaults() {
        val defaultCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        timeInMillis = defaultCalendar.timeInMillis
        isToday = defaultCalendar.after(Calendar.getInstance())
    }

    private fun setupViews() {
        initializeViews(alarm)
        updateSelectedDays()
        binding.alarmSoundTv.text = pickedRingtone?.title
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener { requireActivity().finish() }
        binding.btnSave.setOnClickListener { saveAlarm() }
        soundSettingsContainer.setOnClickListener { openSoundPickerFragment() }
        setupTimePicker()
        setupDaySelection()
        setupRingtoneListener()
    }

    private fun setupTimePicker() {
        binding.timepicker.setOnClickListener {
            val calendar = Calendar.getInstance().apply {
                timeInMillis =
                    if (lastSelectedTime == 0L) getDefaultTimeMillis() else lastSelectedTime
            }
            showTimePicker(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
        }
    }

    private fun showTimePicker(hour: Int, minute: Int) {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Alarm Time")
            .build()

        timePicker.show(parentFragmentManager, "time_picker")
        timePicker.addOnPositiveButtonClickListener {
            updateTimeFromPicker(timePicker.hour, timePicker.minute)
        }
    }

    private fun updateTimeFromPicker(hour24: Int, minute: Int) {
        val amPm = if (hour24 >= 12) "pm" else "am"
        alarm.am_pm = amPm
        val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24

        val selectedCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR, hour12)
            set(Calendar.AM_PM, if (amPm == "pm") Calendar.PM else Calendar.AM)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        lastSelectedTime = selectedCalendar.timeInMillis
        timeInMillis = selectedCalendar.timeInMillis
        isToday =
            selectedCalendar.after(Calendar.getInstance()) || selectedCalendar.timeInMillis == Calendar.getInstance().timeInMillis
        updateSelectedDays()
    }

    private fun setupDaySelection() {
        val selectedDaysContainer = binding.daysLl
        days.forEachIndexed { index, item ->
            val textView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_circle, selectedDaysContainer, false) as TextView
            textView.text = item[0].toString()
            textView.layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                    .apply { weight = 1f }
            textView.setOnLongClickListener {
                isMultiSelectMode = true
                true
            }
            textView.setOnTouchListener(OnSwipeTouchListener(requireContext(),
                onSwipe = { toggleSelection(index, textView) },
                onTap = { toggleSelection(index, textView) }
            ))
            selectedDaysContainer.addView(textView)
        }
    }

    private fun setupRingtoneListener() {
        parentFragmentManager.setFragmentResultListener(
            "ringtone_request",
            viewLifecycleOwner
        ) { _, bundle ->
            pickedRingtone = bundle.getParcelable("selected_ringtone")
            binding.alarmSoundTv.text = pickedRingtone?.title
        }
    }

    private fun saveAlarm() {
        val time = Timeholder(0, 0, "") // Placeholder; actual values set elsewhere if needed
        alarm.Enabled = true
        submitAlarm(time)
        requireActivity().finish()
    }

    private fun initializeViews(alarm: Alarm) {
        binding.alarmSoundTv.text = alarm.sound.soundName
        binding.alarmLabelEt.setText(alarm.label)
        binding.alarmSoundSwtch.isChecked = alarm.sound.soundOn
        binding.alarmVibrationSwtch.isChecked = alarm.vibrate.vibrationOn
        binding.alarmSnoozeSwtch.isChecked = alarm.snooze.snoozeOn
        tvSelectedDays.text = alarm.days
        selectedDays.clear()
        selectedDays.addAll(alarm.days.split(", ").map { days.indexOf(it) })
    }


    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(context: Context, alarm: Alarm) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val alarmId = alarm.alarmId

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmManager", "Permission denied: Cannot schedule exact alarms!")
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }

        val adjustedTimeInMillis = getNextValidTime(alarm)
        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
            putExtra("timeInMillis", adjustedTimeInMillis)
            putExtra("days", alarm.days)
            putExtra("label", alarm.label)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss 'UTC'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        Log.d(
            "wow",
            "Scheduling alarm with ID $alarmId at: ${utcFormat.format(Date(adjustedTimeInMillis))}"
        )
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            adjustedTimeInMillis,
            pendingIntent
        )
    }



    private fun submitAlarm(time: Timeholder) {
        val updatedAlarm = oldAlarm.copy(
            alarmId = oldAlarm.alarmId,
            timeInMillis = timeInMillis,
            label = binding.alarmLabelEt.text.toString(),
            days = tvSelectedDays.text.toString(),
            am_pm = time.ampm,
            sound = Alarm.AlarmSound(
                binding.alarmSoundSwtch.isChecked,
                pickedRingtone?.title ?: "",
                pickedRingtone?.uri.toString()
            ),
            vibrate = Alarm.AlarmVibration(binding.alarmVibrationSwtch.isChecked, ""),
            snooze = Alarm.AlarmSnooze(binding.alarmSnoozeSwtch.isChecked, "Todo"),
            Enabled = true
        )

        context?.let { context ->
            try {
                if (oldAlarm.Enabled) {
                    viewModel.cancelAlarm(context, oldAlarm)
                }
                viewModel.insertOrUpdateAlarm(updatedAlarm, oldAlarm)
                setAlarm(context, updatedAlarm)
            } catch (e: Exception) {
                Log.e("wow", "Failed to submit alarm: ${e.message}")
            }
        } ?: Log.w("wow", "Context is null, cannot submit alarm")
    }

    private fun toggleSelection(index: Int, textView: TextView) {
        val isSelected = selectedDays.contains(index)
        if (isSelected) selectedDays.remove(index) else selectedDays.add(index)
        textView.setTextColor(if (isSelected) Color.BLACK else requireContext().getColor(com.google.android.material.R.color.design_default_color_primary))
        textView.setBackgroundResource(if (isSelected) R.drawable.circle_bg_unselected else R.drawable.circle_bg)
        updateSelectedDays()
    }

    private fun updateSelectedDays() {
        val calendar = Calendar.getInstance()
        val shortDayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)
        val validDays = selectedDays.filter { it in days.indices }

        tvSelectedDays.text = when {
            validDays.isEmpty() -> if (isToday) "Today-$shortDayName,$dateNumber" else {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val tomorrowName =
                    SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                val tomorrowNumber = calendar.get(Calendar.DAY_OF_MONTH)
                "Tomorrow-$tomorrowName,$tomorrowNumber"
            }

            validDays.size == 7 -> "Every day"
            else -> "every ${validDays.sorted().joinToString(",") { days[it] }}"
        }
    }

    private fun openSoundPickerFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.alarm_creation_container, SoundPickerFragment())
            .addToBackStack("alarm_creation")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDefaultTimeMillis(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 6)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

data class Timeholder(
    var hour: Int,
    var minute: Int,
    var ampm: String
)
