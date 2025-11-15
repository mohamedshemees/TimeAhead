package com.example.clock.alarm.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.clock.R
import com.example.clock.databinding.FragmentAlarmEditingBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.Calendar

class AlarmEditingFragment : Fragment() {

    private var _binding: FragmentAlarmEditingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AlarmEditingViewModel by activityViewModels()

    private lateinit var interactionListener: AlarmEditingInteractionListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlarmEditingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        interactionListener = viewModel

        setupViews()
        setupListeners()
        observeViewModel()
    }

    private fun setupViews() {
        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val selectedDaysContainer = binding.daysLl
        days.forEachIndexed { index, item ->
            val textView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_circle, selectedDaysContainer, false) as TextView
            textView.text = item[0].toString()
            selectedDaysContainer.addView(textView)
            textView.setOnClickListener {
                interactionListener.toggleDay(index)
            }
        }
    }

    private fun setupListeners() {
        binding.btnCancel.setOnClickListener { interactionListener.cancel() }
        binding.btnSave.setOnClickListener {
            interactionListener.saveAlarm()
        }
        binding.alarmsoundLl.setOnClickListener { openSoundPickerFragment() }
        binding.timepicker.setOnClickListener { showTimePicker() }

        binding.alarmLabelEt.doOnTextChanged { text, _, _, _ ->
            interactionListener.updateLabel(text.toString())
        }

        binding.alarmSoundSwtch.setOnCheckedChangeListener { _, isChecked ->
            interactionListener.updateSoundOn(isChecked)
        }

        binding.alarmVibrationSwtch.setOnCheckedChangeListener { _, isChecked ->
            interactionListener.updateVibrateOn(isChecked)
        }

        binding.alarmSnoozeSwtch.setOnCheckedChangeListener { _, isChecked ->
            interactionListener.updateSnoozeOn(isChecked)
        }

        parentFragmentManager.setFragmentResultListener("ringtone_request", viewLifecycleOwner) { _, bundle ->
            bundle.getParcelable<SoundPickerFragment.Ringtone>("selected_ringtone")?.let {
                interactionListener.updateRingtone(it)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            state ?: return@observe

            if (binding.alarmLabelEt.text.toString() != state.alarm.label) {
                binding.alarmLabelEt.setText(state.alarm.label)
            }
            binding.alarmSoundTv.text = state.alarm.sound.soundName
            binding.repetition.text = state.selectedDaysText
            binding.alarmSoundSwtch.isChecked = state.alarm.sound.soundOn
            binding.alarmVibrationSwtch.isChecked = state.alarm.vibrate.vibrationOn
            binding.alarmSnoozeSwtch.isChecked = state.alarm.snooze.snoozeOn

            updateDayButtons(state.alarm.getRepeatDays())

            if (state.finishActivity) {
                requireActivity().finish()
            }
        }
    }

    private fun updateDayButtons(selectedDays: List<Int>) {
        for (i in 0 until binding.daysLl.childCount) {
            val textView = binding.daysLl.getChildAt(i) as TextView
            val isSelected = selectedDays.contains(i)
            textView.setTextColor(if (isSelected) Color.WHITE else Color.BLACK)
            textView.setBackgroundResource(if (isSelected) R.drawable.circle_bg else R.drawable.circle_bg_unselected)
        }
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Select Alarm Time")
            .build()

        timePicker.show(parentFragmentManager, "time_picker")
        timePicker.addOnPositiveButtonClickListener {
            interactionListener.updateTime(timePicker.hour, timePicker.minute)
        }
    }

    private fun openSoundPickerFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.alarm_creation_container, SoundPickerFragment())
            .addToBackStack("alarm_creation")
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
