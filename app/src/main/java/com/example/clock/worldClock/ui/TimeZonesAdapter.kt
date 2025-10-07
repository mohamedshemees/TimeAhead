package com.example.clock.worldClock.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.databinding.TimezoneGroupItemBinding
import com.example.clock.databinding.TimezoneHeaderItemBinding
import com.example.clock.databinding.TimezoneItemBinding

class TimeZoneAdapter(
    var items: List<TimeZoneItem> =listOf(),
    private val onItemSelected: (TimeZoneItem.TimeZone) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_GROUP = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TimeZoneItem.Header -> VIEW_TYPE_HEADER
            is TimeZoneItem.TimeZoneGroup -> VIEW_TYPE_GROUP
            is TimeZoneItem.TimeZone -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = TimezoneHeaderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_GROUP -> {
                val binding = TimezoneGroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TimeZoneGroupViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TimeZoneItem.Header -> (holder as HeaderViewHolder).bind(item)
            is TimeZoneItem.TimeZoneGroup -> (holder as TimeZoneGroupViewHolder).bind(item, onItemSelected)
            is TimeZoneItem.TimeZone -> -1
        }
    }

    override fun getItemCount() = items.size

    // Header ViewHolder
    class HeaderViewHolder(private val binding: TimezoneHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TimeZoneItem.Header) {
            binding.header.text = item.text
        }
    }


    class TimeZoneGroupViewHolder(private val binding: TimezoneGroupItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(group: TimeZoneItem.TimeZoneGroup, onItemSelected: (TimeZoneItem.TimeZone) -> Unit) {
            val adapter = InnerTimeZoneAdapter(group.timeZones, onItemSelected)
            binding.timezoneRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.timezoneRecyclerView.adapter = adapter
        }
    }

    fun setTimezoneList(newItems: List<TimeZoneItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

    class InnerTimeZoneAdapter(
        private val timeZones: List<TimeZoneItem.TimeZone>,
        private val onItemSelected: (TimeZoneItem.TimeZone) -> Unit
    ) : RecyclerView.Adapter<InnerTimeZoneAdapter.TimeZoneViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeZoneViewHolder {
            val binding = TimezoneItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TimeZoneViewHolder(binding)
        }

        override fun onBindViewHolder(holder: TimeZoneViewHolder, position: Int) {
            holder.bind(timeZones[position], onItemSelected)
        }

        override fun getItemCount() = timeZones.size

        class TimeZoneViewHolder(private val binding: TimezoneItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(item: TimeZoneItem.TimeZone, onItemSelected: (TimeZoneItem.TimeZone) -> Unit) {
                binding.timezoneName.text = item.label
                binding.timezoneOffset.text = item.offset
                binding.root.setOnClickListener { onItemSelected(item) }
            }
        }

    }





