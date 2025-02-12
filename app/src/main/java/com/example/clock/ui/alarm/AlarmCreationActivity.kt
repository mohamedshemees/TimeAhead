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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.clock.ClockApp
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AlarmCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmCreationBinding
    val viewModel: AlarmViewModel by viewModels<AlarmViewModel> {
        val app= application as ClockApp
        AlarmViewModelFactory(
            app.repository,
            app
        )
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel

        val alarm = intent?.getParcelableExtra("alarm") ?: Alarm()

        // Pass the alarm to the fragment
        val fragment = AlarmEditingFragment().apply {
            arguments = Bundle().apply {
                putParcelable("alarm", alarm)
            }
        }

        // Add or replace the fragment in the container
        supportFragmentManager.beginTransaction()
            .replace(R.id.alarm_creation_container, fragment)
            .commit()
    }
}

