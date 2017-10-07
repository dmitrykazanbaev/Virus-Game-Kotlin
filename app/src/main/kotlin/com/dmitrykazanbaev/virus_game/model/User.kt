package com.dmitrykazanbaev.virus_game.model

import com.dmitrykazanbaev.virus_game.model.virus.VirusManager
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import kotlin.properties.Delegates


class User {
    var balance by Delegates.observable(0) { _, _, newValue ->
        val levelActivity = ApplicationContextHolder.context
        levelActivity.updateBalance(newValue)
    }
    val virus = VirusManager()

    fun getProfit(profit: Int) {
        balance += (profit * virus.abilities.value()).toInt()
    }
}