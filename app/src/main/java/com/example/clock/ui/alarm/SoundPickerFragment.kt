package com.example.clock.ui.alarm

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clock.databinding.FragmentSoundPickerBinding
import com.example.clock.viewmodels.AlarmViewModel
import kotlinx. parcelize.Parcelize


class SoundPickerFragment : Fragment() {
    private val alarmViewModel: AlarmViewModel by activityViewModels()

    private var _binding: FragmentSoundPickerBinding? = null
    private val binding get() = _binding!!

    private var currentRingtone: android.media.Ringtone? = null
    private var selectedRingtoneUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoundPickerBinding.inflate(layoutInflater)

        binding.ringtonesRv.layoutManager = LinearLayoutManager(requireContext())

        // Set up adapter
        var pickedRingtone:Ringtone=alarmViewModel.repository.allRingtones[0]
        val adapter = RingtoneAdapter(alarmViewModel.repository.allRingtones) { ringtone ->
            playRingtone(requireContext(), ringtone.uri)
            selectedRingtoneUri = ringtone.uri
            pickedRingtone=ringtone

        }
        binding.ringtonesRv.adapter = adapter

        binding.BackBtn.setOnClickListener {
            val result = Bundle().apply {
                putParcelable("selected_ringtone", pickedRingtone)
            }
            parentFragmentManager.setFragmentResult("ringtone_request", result)

            parentFragmentManager.popBackStack()
        }

        return binding.root
    }



    fun playRingtone(context: Context, uri: Uri) {
        stopRingtone() // Stop any currently playing ringtone
        currentRingtone = RingtoneManager.getRingtone(context, uri)
        currentRingtone?.play()
    }

    fun stopRingtone() {
        currentRingtone?.stop()
        currentRingtone = null
    }

    override fun onStop() {
        super.onStop()
        stopRingtone() // Stop playback when the fragment is no longer visible
    }
    @Parcelize
    data class Ringtone(val title: String, val uri: Uri): Parcelable


}