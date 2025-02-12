package com.example.clock.ui.alarm

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
import com.example.clock.data.model.Alarm
import com.example.clock.data.model.getDefaultTimeMillis
import com.example.clock.databinding.FragmentAlarmEditingBinding
import com.example.clock.viewmodels.AlarmViewModel
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.properties.Delegates


class AlarmEditingFragment : Fragment() {

    private var _binding: FragmentAlarmEditingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AlarmViewModel by activityViewModels()

    private var isMultiSelectMode = false
    lateinit var tvSelectedDays: TextView
    lateinit var soundSettingsContainer: LinearLayout
    lateinit var vibrationSettingsContainer: LinearLayout
    lateinit var snoozeSettingsContainer: LinearLayout
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var selectedDays = mutableSetOf(0)
    private lateinit var pickedRingtone: SoundPickerFragment.Ringtone
    private var isToday = false
    var timeInMillis = 0L
    lateinit var alarm: Alarm
    lateinit var oldAlarm: Alarm
    lateinit var formattedTime:String

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAlarmEditingBinding.inflate(layoutInflater)
        soundSettingsContainer = binding.alarmsoundLl
        tvSelectedDays = binding.repetition


        oldAlarm = arguments?.getParcelable("alarm") ?: Alarm()
        alarm = oldAlarm.copy()
        initializeViews(alarm)
        updateSelectedDays()


        pickedRingtone = viewModel.repository.allRingtones[0]

        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }

        val clock = binding.timepicker
        val time = Timeholder(6, 0, "AM")

        val defaultCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 6) // Default hour
            set(Calendar.MINUTE, 0) // Default minute
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        timeInMillis = defaultCalendar.timeInMillis
        isToday = defaultCalendar.after(Calendar.getInstance()) // Default check for today
        updateSelectedDays()
        var lastSelectedTime =0L

        clock.setOnClickListener {
            // Use a temporary variable to track the last selected time
            val calendar = Calendar.getInstance().apply {
                timeInMillis = if (lastSelectedTime == 0L) getDefaultTimeMillis() else lastSelectedTime
            }


            val hour = calendar.get(Calendar.HOUR_OF_DAY) // Extract stored hour
            val minute = calendar.get(Calendar.MINUTE) // Extract stored minute


            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Ensure 12-hour format
                .setHour(hour) // Default hour (6 AM)
                .setMinute(minute) // Default minute (00)
                .setTitleText("Select Alarm Time")
                .build()

            timePicker.show(parentFragmentManager, "time_picker")

            timePicker.addOnPositiveButtonClickListener {
                val hour24 = timePicker.hour
                val minute = timePicker.minute

                // Convert to 12-hour format
                val amPm = if (hour24 >= 12) "pm" else "am"
                val hour12 = if (hour24 == 0) 12 else if (hour24 > 12) hour24 - 12 else hour24

                // Format time properly (add leading zero)
                time.hour = hour12
                time.minut = minute
                time.ampm=amPm
                 formattedTime = String.format("%02d:%02d", hour12, minute)

                alarm.timeInMillis = hour12.toLong()

                val currentCalendar = Calendar.getInstance()
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR, hour12) // 12-hour format (0-11)
                    set(Calendar.AM_PM, if (amPm == "pm") Calendar.PM else Calendar.AM)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)

                }
                lastSelectedTime = selectedCalendar.timeInMillis


                timeInMillis = selectedCalendar.timeInMillis

                // Determine if it's today or a future day
                isToday = selectedCalendar.after(currentCalendar)
                        || selectedCalendar.timeInMillis == currentCalendar.timeInMillis
                updateSelectedDays()
            }
        }

        binding.btnSave.setOnClickListener {
            alarm.Enabled=true
            submitAlarm(time)
            requireActivity().finish()
        }
        val selectedDaysContainer = binding.daysLl

        soundSettingsContainer.setOnClickListener {
            openSoundPickerFragment()
        }

        days.forEachIndexed { index, item ->
            val textView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_circle, selectedDaysContainer, false) as TextView
            textView.text = item[0].toString()
            val layoutParams = LinearLayout.LayoutParams(
                0, // width set to 0dp
                LinearLayout.LayoutParams.WRAP_CONTENT // height
            ).apply {
                weight = 1f // Equal weight for distribution
            }
            textView.layoutParams = layoutParams
            isMultiSelectMode = false
            textView.setOnLongClickListener {
                isMultiSelectMode = true
                true
            }

            textView.setOnTouchListener(OnSwipeTouchListener(requireContext(),
                onSwipe = {
                    toggleSelection(index, textView) // Toggle selection on swipe
                },
                onTap = {
                    toggleSelection(index, textView) // Toggle selection on tap
                }
            ))

            selectedDaysContainer.addView(textView)
        }

        parentFragmentManager.setFragmentResultListener(
            "ringtone_request",
            viewLifecycleOwner
        ) { _, bundle ->
            pickedRingtone = bundle.getParcelable("selected_ringtone")!!
            binding.alarmSoundTv.text = pickedRingtone.title

        }
        binding.alarmSoundTv.text = viewModel.repository.allRingtones[0].title

        return binding.root
    }

    private fun initializeViews(alarm: Alarm) {
        binding.alarmSoundTv.text = alarm.sound.soundName
        binding.alarmLabelEt.setText(alarm.label)
        //alarmLabeltv.text=alarm.label
        binding.alarmSoundSwtch.isChecked = alarm.sound.soundOn
        binding.alarmVibrationSwtch.isChecked = alarm.vibrate.vibrationOn
        binding.alarmSnoozeSwtch.isChecked = alarm.snooze.snoozeOn
        tvSelectedDays.text = alarm.days
        selectedDays = alarm.days.split(", ").map { days.indexOf(it) }.toMutableSet()

    }
    private fun submitAlarm(time: Timeholder) {
            val updatedAlarm=oldAlarm.copy(
                timeInMillis = timeInMillis,
                label = binding.alarmLabelEt.text.toString(),
                days = tvSelectedDays.text.toString(),
                am_pm = time.ampm,
                sound = Alarm.AlarmSound(
                    binding.alarmSoundSwtch.isChecked,
                    pickedRingtone.title,
                    pickedRingtone.uri.toString()
                ),
                vibrate = Alarm.AlarmVibration(
                    binding.alarmVibrationSwtch.isChecked, ""
                ),
                snooze = Alarm.AlarmSnooze(
                    snoozeOn = binding.alarmSnoozeSwtch.isChecked,
                    test = "Todo"
                ),
              Enabled =true
            )
            viewModel.insertOrUpdateAlarm(updatedAlarm = updatedAlarm, oldAlarm = oldAlarm)
            context?.let { setAlarm(it,
                "${alarm.timeInMillis}_${alarm.days}_${alarm.label}".
                hashCode()
                ,
                timeInMillis) }


    }


    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(context: Context, alarmId: Int, timeInMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // Check if exact alarms are allowed (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmManager", "Permission denied: Cannot schedule exact alarms!")
            // Prompt user to grant permission (Optional)
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )
        Log.d("AlarmManager", "Setting alarm with ID: $alarmId at time: $timeInMillis")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
        //pendingIntent.cancel()
    }

    fun onItemSelected(selectedItems: Set<Int>) {

    }

    private fun toggleSelection(index: Int, textView: TextView) {

        val isSelected = selectedDays.contains(index)

        if (isSelected) selectedDays.remove(index)
        else selectedDays.add(index)

        textView.setTextColor(if (isSelected) Color.BLACK else requireContext().getColor(com.google.android.material.R.color.design_default_color_primary))
        textView.setBackgroundResource(if (isSelected) R.drawable.circle_bg_unselected else R.drawable.circle_bg)

        updateSelectedDays()

    }

    private fun updateSelectedDays() {
        val calendar = Calendar.getInstance()
        val shortDayName =
            SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
        val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)
        var result = ""
        val validDays = selectedDays.filter { it in days.indices }

        if (validDays.isEmpty()) {
            if (isToday) {
                result = "Today-$shortDayName,$dateNumber"
                tvSelectedDays.text = result
            }
            // No specific days selected
            else {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_MONTH, 1)
                val shortDayName =
                    SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)
                result="Tomorrow-$shortDayName,$dateNumber"
                tvSelectedDays.text  = result
            }
        } else if (validDays.size == 7) {
            result = "Every day"
            tvSelectedDays.text   = result
        }// All days selected
        else result = "every ${validDays.sorted().joinToString(",") { days[it] }} "

        tvSelectedDays.text  = result
    }


    private fun openSoundPickerFragment() {
        val soundPickerFragment = SoundPickerFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.alarm_creation_container, soundPickerFragment)
            .addToBackStack("alarm_creation")
            .commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Helps avoid memory leaks
    }

    private fun openVibrationPickerFragment() {}
    private fun openSnoozePickerFragment() {}
}

data class Timeholder(
    var hour: Int,
    var minut: Int,
    var ampm: String
)




