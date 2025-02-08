package com.example.clock.ui.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.model.Alarm
import com.example.clock.data.repository.AlarmRepository
import com.example.clock.databinding.ActivityAlarmCreationBinding
import com.example.clock.viewmodels.AlarmViewModel
import com.example.clock.viewmodels.AlarmViewModelFactory
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmCreationBinding
    private var isMultiSelectMode = false
    lateinit var tvSelectedDays:TextView

    private lateinit var alarmViewModel: AlarmViewModel
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val selectedDays = mutableSetOf<Int>()
    private  var isToday=false
    var timeInMillis=0L


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlarmCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = AlarmRepository(ClockDataBase.getDatabase(this).alarmDao())
        val factory = AlarmViewModelFactory( repository)

        // ✅ Use factory to create ViewModel
        alarmViewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

        binding.btnCancel.setOnClickListener {
            finish()
        }

        tvSelectedDays=binding.repetition
        val Clock = binding.timepicker
        val time = Timeholder(6, 0, "AM")
        Clock.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H) // Ensure 12-hour format
                .setHour(6) // Default hour (10 AM)
                .setMinute(0) // Default minute (30 minutes)
                .setTitleText("Select Alarm Time")
                .build()
            // Show the TimePicker dialog
            timePicker.show(supportFragmentManager, "time_picker")
            timePicker.addOnPositiveButtonClickListener { view ->
                val hour: Int = timePicker.hour
                val minute: Int = timePicker.minute
                val amPm = if ((hour >= 12)) "PM" else "AM"
                time.hour = hour
                time.minut = minute
                time.ampm = amPm

                val currentCalendar = Calendar.getInstance()
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0) // Reset seconds
                    set(Calendar.MILLISECOND, 0) // Reset milliseconds
                }
                 timeInMillis = selectedCalendar.timeInMillis

                 isToday = when {
                    selectedCalendar.timeInMillis < currentCalendar.timeInMillis -> false
                    else -> true
                }
                updateSelectedDays()
            }
        }

            binding.btnSave.setOnClickListener {
                submitAlarm(time)
            }
            val selectedDaysContainer = binding.daysLl

            days.forEachIndexed { index, item ->
                val textView = LayoutInflater.from(this)
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

                textView.setOnTouchListener(OnSwipeTouchListener(this,
                    onSwipe = {
                        toggleSelection(index, textView) // Toggle selection on swipe
                    },
                    onTap = {
                        toggleSelection(index, textView) // Toggle selection on tap
                    }
                ))

                selectedDaysContainer.addView(textView)
            }
        }





    private fun submitAlarm(time: Timeholder) {
        CoroutineScope(Dispatchers.IO).launch {
            alarmViewModel.insert(
                Alarm(
                    time.hour.toString() + ":" + time.minut.toString(),
                    binding.alarmNameEt.text.toString(),
                    tvSelectedDays.text.toString(),
                    am_pm = time.ampm,
                    sound = Alarm.AlarmSound(
                        binding.alarmSoundSwtch.isChecked,
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString()
                    ),
                    vibrate = Alarm.AlarmVibration(binding.alarmVibrationSwtch.isChecked, ""),
                    Enabled = true
                )
            )
            setAlarm(this@AlarmCreationActivity , 22, timeInMillis)
        }
        finish()
    }


    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(context: Context, alarmId: Int, timeInMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        // Check if exact alarms are allowed (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmManager", "Permission denied: Cannot schedule exact alarms!")
            // Prompt user to grant permission (Optional)
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }

        val alarmIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, alarmId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (timeInMillis < System.currentTimeMillis()) {
            Log.e("AlarmManager", "Cannot set an alarm in the past! Aborting.")
            return
        }
        Log.d("AlarmManager", "Setting alarm with ID: $alarmId at time: $timeInMillis")
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }



    fun onItemSelected(selectedItems: Set<Int>) {
            // Notify or update UI
        }

    private fun toggleSelection(index: Int, textView: TextView) {
            if (selectedDays.contains(index)) {
                // Deselect item
                selectedDays.remove(index)
                textView.setBackgroundResource(R.drawable.circle_bg_unselected) // Default background
            } else {
                // Select item
                selectedDays.add(index)
                textView.setTextColor(resources.getColorStateList(com.google.android.material.R.color.design_default_color_primary))
                textView.setBackgroundResource(R.drawable.circle_bg) // Selected background
            }
            onItemSelected(selectedDays)
            updateSelectedDays()

        }

    private fun updateSelectedDays() {
        if (selectedDays.isEmpty()){

            if (isToday) {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR,0)
                val shortDayName =
                    SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time) // "Wed"
                val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)
                val result = "Today-$shortDayName,$dateNumber"
                tvSelectedDays.text =result
            }
            else {
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_YEAR, 1)

                    val shortDayName = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time) // "Wed"
                    val dateNumber = calendar.get(Calendar.DAY_OF_MONTH)
                    val result = "Tomorrow-$shortDayName,$dateNumber"
                tvSelectedDays.text =result
                }
        }
        else if (selectedDays.size==7){
            tvSelectedDays.text = "Every day"
        }
        else{
            val selectedText = selectedDays.joinToString(", ") { days[it] }
            tvSelectedDays.text = "Every $selectedText"
        }

    }

    data class Timeholder(var hour: Int,
                          var minut: Int,
                          var ampm: String)

}

