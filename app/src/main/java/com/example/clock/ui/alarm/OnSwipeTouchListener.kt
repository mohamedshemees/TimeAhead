package com.example.clock.ui.alarm

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View

class OnSwipeTouchListener(context: Context, private val onSwipe: () -> Unit, private val onTap: () -> Unit) : View.OnTouchListener {

    private val gestureDetector: GestureDetector


    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                // Detect any swipe motion (left, right, up, or down)
                val deltaX = e2.x - (e1?.x ?: 0f)
                val deltaY = e2.y - (e1?.y ?: 0f)
                    onSwipe()
                    return true

            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                onTap()
                return true

            }
        })
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }
}