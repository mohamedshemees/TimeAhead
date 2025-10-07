package com.example.clock.timer.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.clock.R
import com.example.clock.databinding.FragmentDialogBinding
import com.example.clock.timer.domain.TimerPreset

class AddPresetDialogFragment(val onSave: (TimerPreset) -> Unit) : DialogFragment() {
    lateinit var binding: FragmentDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val closeButton = binding.closeButton
        val saveButton = binding.saveButton
        closeButton.setOnClickListener {
            dismiss()
        }
        val hoursEditText = binding.hoursEditText
        val minutesEditText = binding.minutesEditText
        val secondsEditText = binding.secondsEditText
        val nameEditText = binding.nameEditText

        hoursEditText.addTextChangedListener(TimeTextWatcher(hoursEditText, minutesEditText))
        minutesEditText.addTextChangedListener(
            TimeTextWatcher(
                minutesEditText,
                secondsEditText
            )
        )
        secondsEditText.addTextChangedListener(TimeTextWatcher(secondsEditText, null))

        saveButton.setOnClickListener {
            val hours = hoursEditText.text.toString().toIntOrNull() ?: 0
            val minutes = minutesEditText.text.toString().toIntOrNull() ?: 0
            val seconds = secondsEditText.text.toString().toIntOrNull() ?: 0
            onSave(
                TimerPreset(
                    name = nameEditText.text.toString(),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds,
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        super.onStart()
        val dialog = dialog
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation

    }


    inner class TimeTextWatcher(
        private val currentEditText: EditText,
        private val nextEditText: EditText?
    ) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.length == 2) {
                nextEditText?.requestFocus() // Move focus to the next field
            }
        }

        override fun afterTextChanged(s: Editable?) {
            when (currentEditText.id) {
                R.id.hoursEditText -> validateInput(s, 0, 99)
                R.id.minutesEditText, R.id.secondsEditText -> validateInput(s, 0, 59)
            }
        }

        private fun validateInput(s: Editable?, min: Int, max: Int) {
            val value = s?.toString()?.toIntOrNull() ?: return
            if (value < min || value > max) {
                currentEditText.error = "Value must be between $min and $max"
            } else {
                currentEditText.error = null
            }
        }
    }
}
