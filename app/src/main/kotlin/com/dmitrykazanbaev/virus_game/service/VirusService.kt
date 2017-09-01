package com.dmitrykazanbaev.virus_game.service

import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import kotlinx.android.synthetic.main.first_level_activity.*
import java.util.*

fun tryToInfectPhone(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {

        val maxPhonesCanBeInfected: Int = user.virus.devices.phone.value * (level.phones - level.curedPhones) / 100
        if (level.infectedPhones < maxPhonesCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.mobile.value +
                    user.virus.propagation.bluetooth.value +
                    user.virus.propagation.wifi.value) {

                level.infectPhone()
                runOnUiThread { virus_name.text = "${level.infectedPhones}" }
            }
        }
    }
}

fun tryToInfectComputer(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {

        val maxComputersCanBeInfected: Int = user.virus.devices.pc.value * (level.computers - level.curedComputers) / 100
        if (level.infectedComputers < maxComputersCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.ethernet.value +
                    user.virus.propagation.bluetooth.value +
                    user.virus.propagation.wifi.value) {

                level.infectComputer()
            }
        }
    }
}

fun tryToInfectSmartHome(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {

        val maxSmartHomeCanBeInfected: Int = user.virus.devices.smartHome.value * (level.smartHome - level.curedSmartHome) / 100
        if (level.infectedSmartHome < maxSmartHomeCanBeInfected) {

            val random = Random().nextInt(100)
            if (random <= user.virus.propagation.ethernet.value +
                    user.virus.propagation.mobile.value +
                    user.virus.propagation.wifi.value) {

                level.infectSmartHome()
            }
        }
    }
}