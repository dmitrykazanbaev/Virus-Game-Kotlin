package com.dmitrykazanbaev.virus_game.model

import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import kotlin.properties.Delegates

data class Building(val leftSide: Path, val centerSide: Path, val roof: Path) {
    val maxPoint: Point
    val minPoint: Point
    var infectedRoof = Path()

    var computers = 5
    var infectedComputers by Delegates.observable(0) {
        _, oldValue, newValue ->
        if (oldValue == newValue - 1) {
            computers--
            computeInfectedRoof()
        }
    }

    private fun computeInfectedRoof() {
        if (infectedComputers > 0) {

        }
    }

    init {
        val bounds = RectF()
        roof.computeBounds(bounds, false)
        minPoint = Point(bounds.left.toInt(), bounds.top.toInt())
        maxPoint = Point(bounds.left.toInt() + bounds.width().toInt(), bounds.top.toInt() + bounds.height().toInt())
    }
}