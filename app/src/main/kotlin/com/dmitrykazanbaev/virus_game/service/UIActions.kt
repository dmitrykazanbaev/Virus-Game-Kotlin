package com.dmitrykazanbaev.virus_game.service

import android.app.Activity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


fun showCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity
    with(activity) {
        characteristic_window.visibility = View.VISIBLE
        control_buttons.visibility = View.GONE
    }
}

fun closeCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity
    with(activity) {
        characteristic_window.visibility = View.GONE
        control_buttons.visibility = View.VISIBLE
    }
}