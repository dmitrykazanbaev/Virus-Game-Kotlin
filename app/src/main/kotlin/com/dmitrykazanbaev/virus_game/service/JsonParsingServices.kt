package com.dmitrykazanbaev.virus_game.service

import android.graphics.Point
import java.util.*


fun getPointsListFromJsonString(building: String?): List<Point> {
    val list = ArrayList<Point>()

    if (building != null) {
        val coordinates = building.split(",")

        (0 until coordinates.size step 2).mapTo(list) { Point(coordinates[it].toInt(), coordinates[it + 1].toInt()) }
    }

    return list
}