package com.example.clock.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.data.model.Alarm
import com.example.clock.databinding.AlarmItemBinding
import com.example.clock.viewmodels.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmAdapter (private var alarmList: MutableList<Alarm>, private val viewModel: AlarmViewModel):RecyclerView.Adapter<AlarmAdapter.ViewHolder>(){


    class ViewHolder(itemView: AlarmItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val timeTextView: TextView = itemView.timeTv
        val labelTextView: TextView = itemView.titleTv
        val dateTextView: TextView = itemView.dateTv
        val switch: SwitchCompat = itemView.activAlarmSwtch
        val amapm: TextView = itemView.ampmTv


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarmList[position]

        holder.switch.isChecked = alarm.Enabled
        holder.switch.setOnCheckedChangeListener(null)

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            alarm.Enabled = isChecked
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.update(alarm)  // Save the change in Room DB

            }
        }
        holder.timeTextView.text = alarm.time
        holder.labelTextView.text = alarm.label
        holder.dateTextView.text = alarm.days
        holder.amapm.text=alarm.am_pm

    }

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarmList.clear()
        alarmList.addAll(newAlarms)
        notifyDataSetChanged() // Refresh RecyclerView
    }




    override fun getItemCount(): Int =alarmList.size


}