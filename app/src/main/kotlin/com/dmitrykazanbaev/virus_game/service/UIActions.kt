package com.dmitrykazanbaev.virus_game.service

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.dmitrykazanbaev.virus_game.AbstractLevelActivity
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.custom_views.ModificationButton
import kotlinx.android.synthetic.main.level_activity.*


fun showCharacteristicWindow(context: AbstractLevelActivity) {
    with(context) {
        characteristic_window.visibility = View.VISIBLE
        control_buttons.visibility = View.GONE

        radiogroup.check(R.id.devices_button)

        val animation = TranslateAnimation(0f, -background_characteristic_window.width / 2f,
                0f, -background_characteristic_window.height / 2f)
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 16000L

        background_characteristic_window.startAnimation(animation)
    }
}

fun closeCharacteristicWindow(context: AbstractLevelActivity) {
    with(context) {
        characteristic_window.visibility = View.GONE
        control_buttons.visibility = View.VISIBLE

        background_characteristic_window.clearAnimation()
    }
}

fun startNewGame(context: Context) {
    with(context) {
        val intent = Intent(this, FirstLevelActivity::class.java)
        intent.putExtra("new_game", true)
        startActivity(intent)
    }
}

fun continueGame(context: Context) {
    with(context) {
        startActivity(Intent(this, FirstLevelActivity::class.java))
    }
}

fun buyModification(context: AbstractLevelActivity) {
    with(context) {
        val modificationButtonController = modification_controller.getChildAt(0) as ModificationButtonController
        val modificationButton = findViewById(modificationButtonController.checkedRadioButtonId) as ModificationButton
        when (modificationButton.tag) {
            "wifi" -> upgradeAndInvalidate(user.virus.propagation.wifi, modificationButton, modificationButtonController)
            "bluetooth" -> upgradeAndInvalidate(user.virus.propagation.bluetooth, modificationButton, modificationButtonController)
            "ethernet" -> upgradeAndInvalidate(user.virus.propagation.ethernet, modificationButton, modificationButtonController)
            "mobile" -> upgradeAndInvalidate(user.virus.propagation.mobile, modificationButton, modificationButtonController)
            "thief" -> upgradeAndInvalidate(user.virus.abilities.thief, modificationButton, modificationButtonController)
            "control" -> upgradeAndInvalidate(user.virus.abilities.control, modificationButton, modificationButtonController)
            "spam" -> upgradeAndInvalidate(user.virus.abilities.spam, modificationButton, modificationButtonController)
            "invisible" -> upgradeAndInvalidate(user.virus.resistance.invisible, modificationButton, modificationButtonController)
            "mask" -> upgradeAndInvalidate(user.virus.resistance.mask, modificationButton, modificationButtonController)
            "new_virus" -> upgradeAndInvalidate(user.virus.resistance.newVirus, modificationButton, modificationButtonController)
            "phone" -> upgradeAndInvalidate(user.virus.devices.phone, modificationButton, modificationButtonController)
            "pc" -> upgradeAndInvalidate(user.virus.devices.pc, modificationButton, modificationButtonController)
            "smart_home" -> upgradeAndInvalidate(user.virus.devices.smartHome, modificationButton, modificationButtonController)

            else -> return
        }
    }
}