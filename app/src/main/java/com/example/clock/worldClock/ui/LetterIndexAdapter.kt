package com.example.clock.worldClock.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.R
import com.example.clock.databinding.LetterIndexItemBinding

class LetterIndexAdapter(
    private val letters: List<String>,
    private val onLetterSelected: (String) -> Unit
) : RecyclerView.Adapter<LetterIndexAdapter.LetterViewHolder>() {

    var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LetterIndexItemBinding.inflate(inflater, parent, false)
        return LetterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val letter = letters[position]
        holder.bind(letter, position == selectedPosition)

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged() // Refresh selected dot
            onLetterSelected(letter)
        }
    }

    override fun getItemCount(): Int = letters.size

    class LetterViewHolder(private val binding: LetterIndexItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(letter: String, isSelected: Boolean) {
            binding.dot.setImageResource(
                if (isSelected) R.drawable.filled_dot else R.drawable.empty_dot
            )
            binding.letter.text=letter
        }
    }
}
