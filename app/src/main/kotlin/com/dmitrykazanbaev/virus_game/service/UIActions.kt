package com.dmitrykazanbaev.virus_game.service

import android.app.Activity
import android.content.Intent
import android.view.View
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import kotlinx.android.synthetic.main.first_level_activity.*


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

fun startNewGame() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
        startActivity(Intent(this, FirstLevelActivity::class.java))
    }
}