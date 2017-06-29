package com.dmitrykazanbaev.virus_game.model.level

import com.dmitrykazanbaev.virus_game.R


class FirstLevel : AbstractLevel() {
    init {
        initializeLevelWithBuildings()
    }

    override fun initializeLevelWithBuildings() {
        buildings = getBuildings(applicationContext?.resources!!.openRawResource(R.raw.house_fin))
    }
}