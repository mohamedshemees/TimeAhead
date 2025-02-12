package com.example.clock.ui.worldclock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.databinding.FragmentWorldclockBinding
import com.example.clock.viewmodels.WorldClockViewmodel
import java.util.TimeZone

class WorldClockFragment : Fragment() {
    lateinit var binding: FragmentWorldclockBinding
    private val worldClockViewModel: WorldClockViewmodel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorldclockBinding.inflate(inflater, container, false)

       val recyclerView = binding.timzonesRv
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter=ClockAdapter(mutableListOf())

        worldClockViewModel.currentTime.observe(viewLifecycleOwner) {
            binding.currentTimeTv.text = it
        }
        binding.timezone.text= TimeZone.getDefault().displayName

        return binding.root
    }

}
