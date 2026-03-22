package com.example.clock.worldClock.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clock.ClockApp
import com.example.clock.databinding.ActivityTimezonePickerBinding
import com.example.clock.worldClock.WorldClockViewModel
import com.example.clock.utils.getFlattenedTimeZoneList


class TimezonePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimezonePickerBinding
    private lateinit var timeZoneAdapter: TimeZoneAdapter
    private lateinit var letterIndexRecyclerView: RecyclerView
    private lateinit var letterAdapter: LetterIndexAdapter
    private val letters = mutableListOf<String>()
    private lateinit var layoutManager: LinearLayoutManager

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val worldClockViewModel: WorldClockViewModel by lazy {
        (application as ClockApp).worldClockViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimezonePickerBinding.inflate(layoutInflater)

        layoutManager = LinearLayoutManager(this)
        letterIndexRecyclerView = binding.letterIndexRecyclerView
        setContentView(binding.root)

        val timeZoneList = getFlattenedTimeZoneList()
        letters.addAll(timeZoneList.filterIsInstance<TimeZoneItem.Header>().map { it.text })

        letterAdapter = LetterIndexAdapter(letters) { letter -> scrollToLetter(letter) }
        letterIndexRecyclerView.layoutManager = GridLayoutManager(this, letters.size,
            GridLayoutManager.HORIZONTAL, false)
        letterIndexRecyclerView.adapter = letterAdapter



        timeZoneAdapter = TimeZoneAdapter(
            timeZoneList,
        ) {
            worldClockViewModel.inserTimezone(
                it
            )
            finish()
        }

        Log.d("wow",timeZoneList.size.toString())

        binding.timezonesRv.layoutManager = LinearLayoutManager(this)
        binding.timezonesRv.adapter = timeZoneAdapter



        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean =false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }
                searchRunnable= Runnable {
                   filterList(newText ?: "")
                }
                searchHandler.postDelayed(searchRunnable!!,300)
                return true
            }

        })



        binding.timezonesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updateSelectedLetter()
            }
        })

    }

    private fun filterList(s: String) {
        val filteredList =getFlattenedTimeZoneList(s)

        timeZoneAdapter.setTimezoneList(filteredList)

    }

    private fun updateSelectedLetter() {
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        if (firstVisible == RecyclerView.NO_POSITION || firstVisible >= timeZoneAdapter.items.size) {
            return
        }
        for (i in firstVisible downTo 0) {
            val item = timeZoneAdapter.items[i]
            if (item is TimeZoneItem.Header) {
                val letterIndex = letters.indexOf(item.text)
                if (letterIndex != -1) {
                    letterAdapter.selectedPosition = letterIndex
                    letterAdapter.notifyDataSetChanged()
                }
                break
            }
        }
    }

    private fun scrollToLetter(letter: String) {
        val index = timeZoneAdapter.items.indexOfFirst {
            it is TimeZoneItem.Header && it.text == letter
        }
        if (index != -1) {
            binding.timezonesRv.smoothScrollToPosition(index)
        }
    }

    }


