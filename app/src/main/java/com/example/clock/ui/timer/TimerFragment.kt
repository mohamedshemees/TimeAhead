package com.example.clock.ui.timer


import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.clock.databinding.FragmentTimerBinding


class TimerFragment : Fragment() {
    lateinit var binding: FragmentTimerBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(layoutInflater)

        bindViews(binding)


        return binding.root

    }

    private fun bindViews(binding: FragmentTimerBinding) {
        val timerHoures = binding.timerHoures
        val timerMinutes = binding.timerMinutes
        val timerSeconds = binding.timerSeconds

        timerHoures.setFormatter { String.format("%02d", it) }
        timerMinutes.setFormatter { String.format("%02d", it) }
        timerSeconds.setFormatter { String.format("%02d", it) }




            timerHoures.minValue = 0
            timerHoures.maxValue = 99
            timerHoures.wrapSelectorWheel = true
            timerMinutes.minValue = 0
            timerMinutes.maxValue = 59
            timerMinutes.wrapSelectorWheel = true
            timerSeconds.minValue = 0
            timerSeconds.maxValue = 59
            timerSeconds.wrapSelectorWheel = true
        }

    }





