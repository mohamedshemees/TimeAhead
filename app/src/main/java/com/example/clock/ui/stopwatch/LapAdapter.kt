package com.example.clock.ui.stopwatch

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.clock.databinding.LapItemBinding
import androidx.recyclerview.widget.RecyclerView

class LapAdapter(val laps:List<Lap>) : RecyclerView.Adapter<LapAdapter.ViewHolder>() {

    class ViewHolder(itembinding: LapItemBinding) : RecyclerView.ViewHolder(itembinding.root) {
        var lapcount = itembinding.lapNotv
        var laptime = itembinding.laptime
        var lapdate = itembinding.overallTime

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itembindin = LapItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itembindin)
    }


    override fun getItemCount(): Int =laps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lap = laps[position]
        holder.lapcount.text = lap.lapcount.toString()
        holder.laptime.text = lap.laptime
        holder.lapdate.text = lap.lapdate

    }


}
