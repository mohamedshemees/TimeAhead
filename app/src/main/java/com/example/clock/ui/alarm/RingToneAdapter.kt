package com.example.clock.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.R

class RingtoneAdapter(
    private val ringtones: List<SoundPickerFragment.Ringtone>,
    private val onRingtoneSelected: (SoundPickerFragment.Ringtone) -> Unit,

) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.ringtone_item, parent, false)
        return RingtoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        holder.bind(ringtones[position], position)
    }

    override fun getItemCount(): Int = ringtones.size

    inner class RingtoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.ringtone_title_tv)
        private val radioButton: RadioButton = itemView.findViewById(R.id.radio_btn)

        fun bind(ringtone: SoundPickerFragment.Ringtone = ringtones[0], position: Int) {
            titleTextView.text = ringtone.title
            radioButton.isChecked = (position == selectedPosition)
            if (position == 0 && selectedPosition == -1) {

                selectedPosition = 0
            }
            itemView.setOnClickListener {
                onRingtoneSelected(ringtone)
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)

                }
            }
            radioButton.setOnClickListener {
                onRingtoneSelected(ringtone)
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition) // Uncheck previous
                    selectedPosition = position
                    notifyItemChanged(selectedPosition) // Check new
                }
        }
    }
    }
}

