package com.dmitrykazanbaev.virus_game.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.custom_views.ModificationButton
import com.dmitrykazanbaev.virus_game.model.virus.Modification
import kotlinx.android.synthetic.main.level_activity.*


fun showCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
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

fun closeCharacteristicWindow() {
    val activity = ApplicationContextHolder.context as Activity

    with(activity) {
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

fun updateModificationWindow(modification: Modification) {
    val activity = ApplicationContextHolder.context as FirstLevelActivity

    with(activity) {
        if (modification.currentLevel != modification.maxLevel) {
            updateModificationTitle(modification.title)
            updateModificationDescription(modification.upgradeDescription)
            updateModificationUpgradeCost(modification.upgradeCost)

            buy_modification_window.visibility = View.VISIBLE
        } else buy_modification_window.visibility = View.INVISIBLE
    }
}

fun buyModification() {
    val activity = ApplicationContextHolder.context as FirstLevelActivity

    with(activity) {
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

private fun FirstLevelActivity.upgradeAndInvalidate(modification: Modification,
                                                    modificationButton: ModificationButton,
                                                    modificationButtonController: ModificationButtonController) {
    if (user.balance >= modification.upgradeCost) {
        user.balance -= modification.upgradeCost
        modification.upgrade()
        updateModificationWindow(modification)
        modificationButton.modificationLevel = modification.currentLevel
        modificationButtonController.invalidate()
    }
}