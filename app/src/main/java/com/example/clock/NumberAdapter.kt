package com.example.clock


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.databinding.ItemNumberBinding
import com.example.clock.timer.domain.TimerPreset

class NumberAdapter(
    private val numbers: List<TimerPreset>,
    val onClick:(Int)->Unit
) : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

     class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val binding = ItemNumberBinding.bind(itemView)
         private val nameTextView: TextView = binding.nameTextView
         private val numberTextView: TextView = binding.durationTextView

        fun bind(numberItem: TimerPreset, onClick:(Int)->Unit) {
            numberTextView.text = numberItem.hours.toString()
            numberTextView.setOnClickListener {
                onClick(numberItem.hours)
            }
            nameTextView.text = numberItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_number, parent, false)
        return NumberViewHolder(view)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        holder.bind(numbers[position],onClick)
    }

    override fun getItemCount(): Int = numbers.size
}

