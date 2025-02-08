package com.example.clock.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.repository.AlarmRepository
import com.example.clock.databinding.ActivityMainBinding
import com.example.clock.ui.alarm.AlarmReceiver
import com.example.clock.viewmodels.AlarmViewModel
import com.example.clock.viewmodels.AlarmViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var mainActivitybinding : ActivityMainBinding
    lateinit var alarmViewModel: AlarmViewModel
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }


        val repository = AlarmRepository(ClockDataBase.getDatabase(application).alarmDao())
        val factory = AlarmViewModelFactory(repository)

        // ✅ Initialize ViewModel once in MainActivity
        alarmViewModel = ViewModelProvider(this, factory)[AlarmViewModel::class.java]

    }


    }
