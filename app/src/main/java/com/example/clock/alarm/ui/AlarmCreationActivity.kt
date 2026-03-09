package com.example.clock.alarm.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.clock.ClockApp
import com.example.clock.R
import com.example.clock.alarm.AlarmViewModel
import com.example.clock.alarm.AlarmViewModelFactory
import com.example.clock.alarm.domain.Alarm
import com.example.clock.databinding.ActivityAlarmCreationBinding

class AlarmCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlarmCreationBinding

    private val viewModel: AlarmEditingViewModel by viewModels {
        val app = application as ClockApp
        AlarmEditingViewModelFactory(application, app.alarmRepository)
    }

    val alarmViewModel: AlarmViewModel by viewModels {
        val app = application as ClockApp
        AlarmViewModelFactory(
            app.alarmRepository, app
        )
    }
    private lateinit var interactionListener: AlarmEditingInteractionListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        interactionListener = viewModel

        if (savedInstanceState == null) {
            val alarm = intent?.getParcelableExtra<Alarm>("alarm") ?: Alarm()
            interactionListener.initAlarm(alarm)
            supportFragmentManager.beginTransaction()
                .replace(R.id.alarm_creation_container, AlarmEditingFragment())
                .commit()
        }
    }
}
