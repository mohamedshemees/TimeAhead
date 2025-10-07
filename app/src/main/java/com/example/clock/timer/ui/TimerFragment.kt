package com.example.clock.timer.ui


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.NumberAdapter
import com.example.clock.databinding.FragmentTimerBinding
import com.example.clock.timer.domain.TimerPreset
import kotlin.concurrent.timer


class TimerFragment : Fragment() {
    lateinit var binding: FragmentTimerBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var adapter: NumberAdapter
    lateinit var timeHoures:NumberPicker
    lateinit var timeMinutes:NumberPicker
    lateinit var timeSeconds:NumberPicker
    lateinit var addPreset:ImageButton
    lateinit var deletePreset:ImageButton

    private val timerViewModel: TimerViewModel by activityViewModels<TimerViewModel>()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimerBinding.inflate(layoutInflater)

        timeHoures = binding.timerHoures
        timeMinutes = binding.timerMinutes
        timeSeconds = binding.timerSeconds

        bindViews()
        addPreset = binding.addPresetBtn
        addPreset.setOnClickListener {
            val dialog = AddPresetDialogFragment{

                timerViewModel.insert(it)
            }
            dialog.show(parentFragmentManager, "AddPresetDialog")
        }

        val numbers = (1..10).map {

        }


         recyclerView = binding.NumberRv
        recyclerView.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

//         adapter = NumberAdapter(
//             timerViewModel.allPresets.observe(viewLifecycleOwner)
//         )
        recyclerView.adapter = adapter

        return binding.root

    }



    private fun bindViews() {
        listOf(timeHoures,timeMinutes, timeSeconds).forEach { picker ->
            picker.setFormatter { String.format("%02d", it) }
            picker.wrapSelectorWheel = true
        }
        binding.timerHoures.setupPicker(0, 99)
        binding.timerMinutes.setupPicker(0, 59)
        binding.timerSeconds.setupPicker(0, 59)
    }

    private fun NumberPicker.setupPicker(min: Int, max: Int) {
        minValue = min
        maxValue = max
    }

}









