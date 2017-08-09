package com.dmitrykazanbaev.virus_game.service

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import kotlinx.android.synthetic.main.first_level_activity.*


fun showCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
        characteristic_window.visibility = View.VISIBLE
        control_buttons.visibility = View.GONE

        val animation = TranslateAnimation(0f, -background_characteristic_window.width / 2f,
                0f, -background_characteristic_window.height / 2f)
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 16000L

        background_characteristic_window.startAnimation(animation)
    }
}

fun closeCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
        characteristic_window.visibility = View.GONE
        control_buttons.visibility = View.VISIBLE

        background_characteristic_window.clearAnimation()
    }
}

fun startNewGame() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
        val intent = Intent(this, FirstLevelActivity::class.java)
        intent.putExtra("new_game", true)
        startActivity(intent)
    }
}

fun continueGame() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
        startActivity(Intent(this, FirstLevelActivity::class.java))
    }
}

fun modbutton() {
    Log.w("dmka", "click")
}