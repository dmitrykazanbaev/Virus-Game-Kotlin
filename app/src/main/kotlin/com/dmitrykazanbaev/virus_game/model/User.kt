package com.dmitrykazanbaev.virus_game.model

import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.model.virus.Virus
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import kotlin.properties.Delegates


class User {
    var balance by Delegates.observable(0) { _, _, newValue ->
        val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity
        firstLevelActivity.updateBalance(newValue)
    }
    val userVirus = Virus()
}