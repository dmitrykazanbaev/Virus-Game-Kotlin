package com.dmitrykazanbaev.virus_game.model.level

import com.beust.klaxon.JsonObject
import com.dmitrykazanbaev.virus_game.R


class FirstLevel : AbstractLevel() {
    init {
        initializeLevelWithBuildings()
    }

    override fun initializeLevelWithBuildings() {
        val jsonBuildings = getJsonBuildings(applicationContext?.resources!!.openRawResource(R.raw.house_fin))

        jsonBuildings.forEach {
            buildings.add(getBuilding(it as JsonObject))
        }
    }
}