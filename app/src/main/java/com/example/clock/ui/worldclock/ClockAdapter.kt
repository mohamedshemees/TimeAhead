package com.example.clock.ui.worldclock

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.example.clock.data.model.Alarm
import com.example.clock.databinding.AlarmItemBinding
import com.example.clock.databinding.ClockItemBinding
import com.example.clock.viewmodels.AlarmViewModel
import com.example.clock.viewmodels.WorldClockViewmodel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.clock.data.model.WorldClock

class ClockAdapter (private var alarmList: MutableList<WorldClock>):RecyclerView.Adapter<ClockAdapter.ViewHolder>(){


    class ViewHolder(itemView: ClockItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val clock: TextView = itemView.clock


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ClockItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clock = alarmList[position]

        holder.clock.text = clock.timezone


    }

    fun updateAlarms(newAlarms: List<WorldClock>) {
        alarmList.clear()
        alarmList.addAll(newAlarms)
        notifyDataSetChanged() // Refresh RecyclerView
    }




    override fun getItemCount(): Int =alarmList.size


}