package com.dmitrykazanbaev.virus_game.model.level

import android.content.res.Resources
import android.util.Log
import com.beust.klaxon.JsonObject
import com.dmitrykazanbaev.virus_game.R


class FirstLevel : AbstractLevel() {
    init {
        val buildings = getBuildings(applicationContext?.resources!!.openRawResource(R.raw.house_fin))
//        buildings.forEach {
//            drawBuilding(it as JsonObject, canvas)
//        }
        Log.w("dmka", buildings.toJsonString())
    }
}