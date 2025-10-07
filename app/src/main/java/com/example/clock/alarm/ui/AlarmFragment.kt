package com.example.clock.alarm.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.R
import com.example.clock.databinding.FragmentAlarmBinding
import com.example.clock.alarm.AlarmViewModel
import com.example.clock.alarm.ui.adapters.AlarmAdapter

class AlarmFragment : Fragment() {
    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    private val alarmViewModel: AlarmViewModel by activityViewModels()

    private lateinit var deleteButton: ImageButton
    var alarmscount:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)

        val recyclerView = binding.alarmsRv
        deleteButton = binding.deleteAlarmBtn
        val adapter = AlarmAdapter(
            mutableListOf(),
            alarmViewModel,
            onAlarmLongClick = { isSelectionMode ->
                recyclerView.elevation=if (isSelectionMode) 10f else 0f
                recyclerView.alpha = if (isSelectionMode) 0.5f else 1f
                recyclerView.setPadding(30)
                deleteButton.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            },
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        alarmViewModel.allAlarms.observe(viewLifecycleOwner) { alarms ->
            adapter.updateAlarms(alarms)
            alarmscount=alarms.size
        }
        deleteButton.setOnClickListener {
            val selectedAlarms = adapter.getSelectedAlarms()
            alarmViewModel.deleteAlarms(selectedAlarms)
            adapter.clearSelection()
            deleteButton.visibility = View.GONE
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
        val menuButton = binding.menuButton
        menuButton.setOnClickListener {
            // Create the PopupMenu
            val popupMenu = PopupMenu(requireContext(),
                menuButton,0,0,
                R.style.custompopupmenu)

            val menuResId = when (alarmscount) {
                0 -> R.menu.alarm_options_empty
                2 -> R.menu.alarm_options_morethan_1
                else -> R.menu.alarm_options1
            }
            popupMenu.menuInflater.inflate(menuResId, popupMenu.menu)

            popupMenu.show()
        }

    }
    }


