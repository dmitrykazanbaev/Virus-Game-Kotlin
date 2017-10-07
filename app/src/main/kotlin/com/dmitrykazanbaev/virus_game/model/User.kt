package com.dmitrykazanbaev.virus_game.model

import com.dmitrykazanbaev.virus_game.AbstractLevelActivity
import com.dmitrykazanbaev.virus_game.model.virus.VirusManager
import kotlin.properties.Delegates


class User(val context: AbstractLevelActivity) {
    var balance by Delegates.observable(0) { _, _, newValue ->
        context.updateBalance(newValue)
    }
    val virus = VirusManager(context)

    fun getProfit(profit: Int) {
        balance += (profit * virus.abilities.value()).toInt()
    }
}