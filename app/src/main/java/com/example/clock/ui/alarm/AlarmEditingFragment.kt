package com.example.clock.ui.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.clock.R
import com.example.clock.databinding.ActivityAlarmCreationBinding
import com.example.clock.databinding.FragmentAlarmBinding
import com.example.clock.viewmodels.AlarmViewModel


class AlarmEditingFragment : Fragment() {

    private var _binding: AlarmEditingFragment? = null
    private val binding get() = _binding!!

    private var isMultiSelectMode = false
    lateinit var tvSelectedDays: TextView

    private lateinit var alarmViewModel: AlarmViewModel
    private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val selectedDays = mutableSetOf<Int>()
    private  var isToday=false
    var timeInMillis=0L



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_editing, container, false)
    }
}
