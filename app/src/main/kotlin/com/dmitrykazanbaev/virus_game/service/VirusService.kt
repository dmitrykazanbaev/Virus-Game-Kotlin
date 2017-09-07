package com.dmitrykazanbaev.virus_game.service

import android.view.View
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
                if (level.infectedPhones == 1)
                    firstLevelActivity.firstLevelView.coinButtonView.showCoin(115)
                user.getProfit(level.profitPhone)
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
                user.getProfit(level.profitComputer)
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
                user.getProfit(level.profitSmartHome)
            }
        }
    }
}

fun tryToDetectVirus(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {
        if (level.detectedDevices < level.countDetectedDevicesForStartAntivirusDevelopment) {

            val allDevices = level.computers + level.smartHome + level.phones
            val infectedDevices = level.infectedComputers + level.infectedSmartHome + level.infectedPhones
            val resistance = user.virus.resistance.mask.value
            val detection = user.virus.abilities.detection()
            val random = Random().nextInt(allDevices * resistance)

            if (random < detection * infectedDevices * level.levelCoefficient) {
                level.detectedDevices++
                runOnUiThread { virus_button.text = "${level.detectedDevices}" }
            }
        }
    }
}

fun tryToProgressAntivirus(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {

        if (level.detectedDevices == level.countDetectedDevicesForStartAntivirusDevelopment &&
                level.antivirusProgress < 100) {

            val allDevices = level.computers + level.smartHome + level.phones
            val infectedDevices = level.infectedComputers + level.infectedSmartHome + level.infectedPhones
            val resistance = user.virus.resistance.mask.value
            val detection = user.virus.abilities.detection()
            val random = Random().nextInt(allDevices * resistance)

            if (random < detection * infectedDevices * level.levelCoefficient) {
                level.antivirusProgress++
                runOnUiThread { antivirus_button.text = "${level.antivirusProgress}%  " }
            }

            runOnUiThread { antivirus.visibility = View.VISIBLE }
        }
    }
}

fun tryToCureDevices(level: FirstLevel) {
    val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity

    with(firstLevelActivity) {

        if (level.antivirusProgress == 100) {

            level.curePhone()
            level.cureComputer()
            level.cureSmartHome()
        }
    }
}