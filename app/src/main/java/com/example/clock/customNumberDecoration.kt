package com.example.clock

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class customNumberDecoration : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            val center = parent.width / 2
            val offset = (center - view.width / 2).toFloat()

            if (position == 1 || position == 3) { // Adjust positions as needed
                view.scaleX = 0.8f
                view.scaleY = 0.8f
            } else if (position == 2) { // Center item
                view.scaleX = 1.2f
                view.scaleY = 1.2f
            }

    }
}