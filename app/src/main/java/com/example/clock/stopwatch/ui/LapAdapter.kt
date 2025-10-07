package com.example.clock.stopwatch.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.clock.databinding.LapItemBinding
import androidx.recyclerview.widget.RecyclerView

class LapAdapter(private val laps: List<Lap>) : RecyclerView.Adapter<LapAdapter.ViewHolder>() {

    class ViewHolder(itemBinding: LapItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {
        val lapCountTv = itemBinding.lapNotv
        val lapTimeTv = itemBinding.laptime
        val overallDurationTv = itemBinding.overallTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = LapItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = laps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lap = laps[position]
        holder.lapCountTv.text = lap.lapCount.toString()
        holder.lapTimeTv.text = lap.lapTime
        lap.lapTime>laps.maxOf { it.lapTime }
        holder.lapTimeTv.setTextColor(Color.RED)

        holder.overallDurationTv.text = lap.overallDuration

    }
}