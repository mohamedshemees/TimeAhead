package com.example.clock.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.clock.ClockApp
import com.example.clock.data.local.AlarmDao
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.repository.AlarmRepository
import com.example.clock.databinding.ActivityMainBinding
import com.example.clock.ui.alarm.AlarmReceiver
import com.example.clock.viewmodels.AlarmViewModel
import com.example.clock.viewmodels.AlarmViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Arrays
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    lateinit var mainActivitybinding : ActivityMainBinding

    val viewModel: AlarmViewModel by viewModels {

        val app = application as ClockApp

        AlarmViewModelFactory(
            app.repository,
            app
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mainActivitybinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivitybinding.root)

        val tabLayout = mainActivitybinding.tabLayout
        val viewPager = mainActivitybinding.viewPager

        val viewPagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Alarm"
                1 -> "World Clock"
                2 -> "Stopwatch"
                3 -> "Timer"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }


        viewModel.getAllAlarms()

        CoroutineScope(Dispatchers.IO).launch {
        viewModel.repository.getSystemRingtones(context = this@MainActivity)
    }

    }


    }
