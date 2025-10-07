package com.example.clock.alarm.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.clock.ClockApp
import com.example.clock.R
import com.example.clock.alarm.domain.Alarm
import com.example.clock.databinding.ActivityAlarmCreationBinding
import com.example.clock.alarm.AlarmViewModel
import com.example.clock.alarm.AlarmViewModelFactory


class AlarmCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmCreationBinding

    private val viewModel: AlarmViewModel by viewModels<AlarmViewModel> {
        val app= application as ClockApp
        AlarmViewModelFactory(
            app.alarmRepository,
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

        val fragment = AlarmEditingFragment().apply {
            arguments = Bundle().apply {
                putParcelable("alarm", alarm)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.alarm_creation_container, fragment)
            .commit()
    }
}

