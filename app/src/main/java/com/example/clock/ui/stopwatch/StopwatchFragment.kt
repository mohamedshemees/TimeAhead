package com.example.clock.ui.stopwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.R
import com.example.clock.databinding.FragmentStopwatchBinding
import com.example.clock.viewmodels.StopWatchViewModel
import kotlinx.coroutines.launch

class StopwatchFragment : Fragment() {
    lateinit var binding: FragmentStopwatchBinding
    val stopWatchViewModel: StopWatchViewModel by viewModels()
    lateinit var startbtn: Button
    lateinit var lapbtn: Button
    lateinit var time: TextView
    lateinit var laptime: TextView
    lateinit var lapsrv: RecyclerView
    lateinit var lapsDetailsContainer:ConstraintLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStopwatchBinding.inflate(inflater, container, false)
        startbtn = binding.startbtn
        lapbtn = binding.lapbtn
        time = binding.time
        lapsrv = binding.lapsRv
        laptime = binding.laptime
        lapsDetailsContainer=binding.lapsDetailsCl


        startbtn.setOnClickListener {
            stopWatchViewModel.toggleCounter()
        }

        stopWatchViewModel.counterOn.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                startbtn.text = "Stop"
                startbtn.setBackgroundColor(resources.getColor(R.color.red))
                lapbtn.text = "Lap"
            } else {
                startbtn.text = "Start"
                lapbtn.text = "Reset"
                startbtn.setBackgroundColor(androidx.appcompat.R.attr.colorButtonNormal)
                updateStartButtonState(stopWatchViewModel.counter.value ?: 0L)
            }
        }
        lifecycleScope.launch {
            stopWatchViewModel.counterformatted.asFlow().collect {
                time.text = it
            }
        }

        lifecycleScope.launch {
            stopWatchViewModel.lapformatted.asFlow().collect {
                laptime.text = it
            }
        }
        lapsrv.layoutManager = LinearLayoutManager(requireContext())

        stopWatchViewModel.laps.observe(viewLifecycleOwner) {
            lapsrv.adapter = LapAdapter(it)
        }
        lapbtn.setOnClickListener {
                updateLapsVisibality()
                stopWatchViewModel.addlap()
        }
        return binding.root
    }

    private fun updateLapsVisibality(){
        if (stopWatchViewModel.counterOn.value == true){
            laptime.visibility=View.VISIBLE
            lapsDetailsContainer.visibility=View.VISIBLE
        }
        else{
            lapsDetailsContainer.visibility=View.GONE
            laptime.visibility=View.GONE
            stopWatchViewModel.resetCounter()
        }
    }

    private fun updateStartButtonState(time: Long) {
        if (time == 0L) {
            startbtn.text = "Start"
            startbtn.setBackgroundColor(androidx.appcompat.R.attr.colorButtonNormal)
            lapbtn.text = "Reset"
        } else {
            startbtn.text = "Resume"
            startbtn.setBackgroundColor(androidx.appcompat.R.attr.colorButtonNormal)
            lapbtn.text = "Reset"

        }
    }


}
