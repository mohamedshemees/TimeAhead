package com.example.clock.ui.alarm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.R
import com.example.clock.data.local.ClockDataBase
import com.example.clock.data.model.Alarm
import com.example.clock.data.repository.AlarmRepository
import com.example.clock.databinding.FragmentAlarmBinding
import com.example.clock.ui.MainActivity
import com.example.clock.viewmodels.AlarmViewModel
import com.example.clock.viewmodels.AlarmViewModelFactory
import kotlin.math.max
import kotlin.math.min

class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val alarmViewModel: AlarmViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)

        val textView = binding.alarmTatusTv
        textView.text = "Hello, Alarm Fragment!"

        val application = requireActivity().application
        val repository = AlarmRepository(ClockDataBase.getDatabase(application).alarmDao())
        val factory = AlarmViewModelFactory( repository)

        // ✅ Use factory to create ViewModel

        val recyclerView = binding.alarmsRv

        val adapter = AlarmAdapter(mutableListOf(), alarmViewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        alarmViewModel.allAlarms.observe(viewLifecycleOwner) { alarms ->
            adapter.updateAlarms(alarms)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addAlarmBtn = binding.addAlarmBtn
        addAlarmBtn.setOnClickListener{
            val intent = Intent(requireContext(), AlarmCreationActivity::class.java)
            startActivity(intent)

        }
        binding.alarmNsv.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val fadeAmount = 1 - (scrollY.toFloat() / 300)
            binding.alarmTatusTv.alpha = fadeAmount.coerceIn(0f, 1f)
        }
        val menuButton = binding.menuButton
        menuButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), menuButton)
            val alarmCount = 2
             when(alarmCount){
                  0 -> popupMenu.menuInflater.inflate(R.menu.alarm_options_empty, popupMenu.menu)
                  2 -> popupMenu.menuInflater.inflate(R.menu.alarm_options_morethan_1, popupMenu.menu)
                 else -> popupMenu.menuInflater.inflate(R.menu.alarm_options1, popupMenu.menu)
             }

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.set_bedtime -> {
                        Toast.makeText(requireContext(), "Option 1 clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.edit -> {
                        Toast.makeText(requireContext(), "Option 2 clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.sort -> {
                        Toast.makeText(requireContext(), "Option 3 clicked", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            // Show the menu
            popupMenu.show()
        }


    }
    }


