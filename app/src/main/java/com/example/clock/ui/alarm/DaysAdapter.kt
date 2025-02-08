package com.example.clock.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.databinding.DayItemBinding

class DaysAdapter(private val daysList: List<String>, private val onItemClicked: (Int) -> Unit)
    : RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    private val selectedDays = mutableSetOf<Int>() // To keep track of selected days

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val binding = DayItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val dayName = daysList[position]
        holder.bind(dayName)

        holder.itemView.setOnClickListener {
            // Toggle selection when item is clicked
            if (selectedDays.contains(position)) {
                selectedDays.remove(position)
            } else {
                selectedDays.add(position)
            }
            notifyItemChanged(position)  // Notify adapter to update this item
            onItemClicked(position) // Optional: perform any action when a day is selected
        }
    }

    override fun getItemCount(): Int = daysList.size

    inner class DayViewHolder(private val binding: DayItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dayName: String) {
            binding.daynameTv.text = dayName

        }
    }
}