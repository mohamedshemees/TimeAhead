package com.example.clock.alarm.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.R
import com.example.clock.alarm.domain.Alarm
import com.example.clock.databinding.AlarmItemBinding
import com.example.clock.alarm.ui.AlarmCreationActivity
import java.util.Calendar

class AlarmAdapter(
    private var alarmList: MutableList<Alarm>,
    private val onAlarmToggled: (Alarm) -> Unit,
    private val onAlarmLongClick: (Boolean) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.ViewHolder>() {
    private val selectedAlarms = mutableSetOf<Alarm>()
    private var isSelectionMode = false
    lateinit var context: Context

    class ViewHolder(itemView: AlarmItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val timeTextView: TextView = itemView.timeTv
        val labelTextView: TextView = itemView.titleTv
        val dateTextView: TextView = itemView.dateTv
        val switch: SwitchCompat = itemView.activAlarmSwtch
        val amapm: TextView = itemView.ampmTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AlarmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val alarm = alarmList[position]

        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = alarm.isEnabled
       /* holder.switch.setOnCheckedChangeListener { _, isChecked ->
            alarm.isEnabled = isChecked
            onAlarmToggled(alarm)
        }*/

        holder.timeTextView.text = convertMillisToTimeWithCalendar(alarm.timeInMillis)
        holder.labelTextView.text = alarm.label
        holder.dateTextView.text = alarm.days
        holder.amapm.text = alarm.amPm

        if (holder.labelTextView.text.isEmpty()) {
            holder.labelTextView.visibility = View.GONE
        } else {
            holder.labelTextView.visibility = View.VISIBLE
        }

        if (alarm.isEnabled) {
            holder.timeTextView.setTextColor(Color.BLACK)
            holder.dateTextView.setTextColor(Color.BLACK)
            holder.amapm.setTextColor(Color.BLACK)
            holder.labelTextView.setTextColor(Color.BLACK)
        } else {
            holder.timeTextView.setTextColor(Color.GRAY)
            holder.dateTextView.setTextColor(Color.GRAY)
            holder.labelTextView.setTextColor(Color.GRAY)
            holder.amapm.setTextColor(Color.GRAY)
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(alarm)
            } else {
                val intent = Intent(holder.itemView.context, AlarmCreationActivity::class.java)
                intent.putExtra("alarm", alarm.alarmId)
                holder.itemView.context.startActivity(intent)
            }
        }

        val backgroundRes = if (selectedAlarms.contains(alarm)) {
            R.drawable.rounded_selected_background
        } else {
            R.drawable.rounded_background
        }
        holder.itemView.setBackgroundResource(backgroundRes)

        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                onAlarmLongClick(true)
            }
            toggleSelection(alarm)
            true
        }
    }

    private fun toggleSelection(alarm: Alarm) {
        if (selectedAlarms.contains(alarm)) {
            selectedAlarms.remove(alarm)
        } else {
            selectedAlarms.add(alarm)
        }

        if (selectedAlarms.isEmpty()) {
            isSelectionMode = false
            onAlarmLongClick(false)
        }
        notifyDataSetChanged()
    }

    fun getSelectedAlarms(): List<Alarm> {
        return selectedAlarms.toList()
    }

    fun clearSelection() {
        selectedAlarms.clear()
        isSelectionMode = false
        notifyDataSetChanged()
    }

    private fun convertMillisToTimeWithCalendar(millis: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        val hour = calendar.get(Calendar.HOUR) // 12-hour format (0-11)
        val minute = calendar.get(Calendar.MINUTE)

        val hourFormatted = if (hour == 0) 12 else hour // Convert 0 to 12 for AM/PM
        return String.format("%2d:%02d", hourFormatted, minute)
    }

    override fun getItemCount(): Int = alarmList.size

    fun updateAlarms(newAlarms: List<Alarm>) {
        alarmList.clear()
        alarmList.addAll(newAlarms)
        notifyDataSetChanged() // Refresh RecyclerView
    }
}