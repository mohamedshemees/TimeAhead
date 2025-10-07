package com.example.clock.worldClock.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.databinding.FragmentWorldclockBinding
import com.example.clock.worldClock.WorldClockViewModel
import java.util.TimeZone

class WorldClockFragment : Fragment() {
    lateinit var binding: FragmentWorldclockBinding

    private val worldClockViewModel: WorldClockViewModel by activityViewModels<WorldClockViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorldclockBinding.inflate(inflater, container, false)

       val recyclerView = binding.timzonesRv
        recyclerView.layoutManager = LinearLayoutManager(context)
        val timzoneadapter= SelectedCountriesAdapter(
            mutableListOf()
        ) {
            binding.deleteTimezoneBtn.visibility = View.VISIBLE
        }
        binding.deleteTimezoneBtn.setOnClickListener {
            val selectedTimeZones = timzoneadapter.getSelectedTimeZones()
            worldClockViewModel.deleteTimzone(selectedTimeZones)
            timzoneadapter.clearSelection()
            binding.deleteTimezoneBtn.visibility = View.GONE
        }
        recyclerView.adapter = timzoneadapter


        worldClockViewModel.timeZoneList.observe(viewLifecycleOwner) { timzones ->
            timzoneadapter.updateTimeZones(timzones ?: emptyList())

        }
        Log.d("wow", "onCreateviewmodel instance: ${worldClockViewModel}")


        worldClockViewModel.currentTime.observe(viewLifecycleOwner) {
            binding.currentTimeTv.text = it

        }
        binding.timezone.text= TimeZone.getDefault().displayName


        binding.addTimezoneBtn.setOnClickListener {
            val intent = Intent(context, TimezonePickerActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

}
