package com.example.clock.alarm.ui

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.databinding.FragmentSoundPickerBinding
import com.example.clock.alarm.ui.adapters.RingtoneAdapter

class SoundPickerFragment : Fragment() {
    private val alarmViewModel: AlarmEditingViewModel by activityViewModels()

    private var _binding: FragmentSoundPickerBinding? = null
    private val binding get() = _binding!!

    private var currentRingtone: android.media.Ringtone? = null
    private var selectedRingtoneUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoundPickerBinding.inflate(inflater, container, false)

        binding.ringtonesRv.layoutManager = LinearLayoutManager(requireContext())

        // Observe UI state for ringtones
        alarmViewModel.uiState.observe(viewLifecycleOwner) { state ->
            val ringtones = state.ringtones
            if (ringtones.isNotEmpty()) {
                val adapter = RingtoneAdapter(ringtones) { ringtone ->
                    playRingtone(requireContext(), ringtone.uri)
                    selectedRingtoneUri = ringtone.uri
                    alarmViewModel.updateRingtone(ringtone)
                }
                binding.ringtonesRv.adapter = adapter
            }
        }

        binding.BackBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    fun playRingtone(context: Context, uri: Uri) {
        stopRingtone()
        currentRingtone = RingtoneManager.getRingtone(context, uri)
        currentRingtone?.play()
    }

    fun stopRingtone() {
        currentRingtone?.stop()
        currentRingtone = null
    }

    override fun onStop() {
        super.onStop()
        stopRingtone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
