package com.example.clock.worldClock.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.clock.R
import com.example.clock.databinding.ClockItemBinding

class SelectedCountriesAdapter (private var TimzoneList:
                                MutableList<TimeZoneItem.TimeZone>,
                                private var onTimeZoneLongClick: (Boolean) -> Unit
    )

    :RecyclerView.Adapter<SelectedCountriesAdapter.ViewHolder>(){

    private var isSelectionMode = false
    private val selectedTimeZones = mutableSetOf<TimeZoneItem.TimeZone>()
    class ViewHolder(itemView: ClockItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val clock: TextView = itemView.clockTv
        val offset: TextView = itemView.offsetTv
        val currentTime: TextView = itemView.timeTv


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clock = TimzoneList[position]

        holder.clock.text = clock.label
        holder.offset.text = clock.offset
        holder.currentTime.text=clock.currentTime

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {

                toggleSelection(clock)
            } else {

            }
        }
        val backgroundRes = if (selectedTimeZones.contains(clock)) {
            R.drawable.rounded_selected_background
        } else {
            R.drawable.rounded_background
        }
        holder.itemView.setBackgroundResource(backgroundRes)


        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                onTimeZoneLongClick(true)
            }
            toggleSelection(clock)
            true
        }
    }

    fun getSelectedTimeZones(): List<TimeZoneItem.TimeZone> {
        return selectedTimeZones.toList()
    }

    fun clearSelection() {
        selectedTimeZones.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }
    private fun toggleSelection(alarm: TimeZoneItem.TimeZone) {
        if (selectedTimeZones.contains(alarm)) {
            selectedTimeZones.remove(alarm)
        } else {
            selectedTimeZones.add(alarm)
        }


        if (selectedTimeZones.isEmpty()) {
            isSelectionMode = false
            onTimeZoneLongClick(false)
        }
        notifyDataSetChanged()
    }

    fun updateTimeZones(newTimeZones: List<TimeZoneItem.TimeZone>) {
        TimzoneList.clear()
        TimzoneList.addAll(newTimeZones)
        notifyDataSetChanged()
    }




    override fun getItemCount(): Int =TimzoneList.size


}