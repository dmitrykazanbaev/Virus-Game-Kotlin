package com.dmitrykazanbaev.virus_game.service

import android.app.Activity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.R


fun showCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity
    activity
            .findViewById<RelativeLayout>(R.id.characteristic_window)
            .visibility = View.VISIBLE

    activity
            .findViewById<LinearLayout>(R.id.control_buttons)
            .visibility = View.GONE
}

fun closeCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity
    activity
            .findViewById<RelativeLayout>(R.id.characteristic_window)
            .visibility = View.GONE

    activity
            .findViewById<LinearLayout>(R.id.control_buttons)
            .visibility = View.VISIBLE
}