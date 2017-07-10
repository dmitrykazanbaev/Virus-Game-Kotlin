package com.dmitrykazanbaev.virus_game.model

import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF

data class Building(val leftSide: Path, val centerSide: Path, val roof: Path) {
    val maxPoint: Point
    val minPoint: Point

    var computers = 30
    var infectedComputers = 0

    init {
        val bounds = RectF()
        roof.computeBounds(bounds, false)
        minPoint = Point(bounds.left.toInt(), bounds.top.toInt())
        maxPoint = Point(bounds.left.toInt() + bounds.width().toInt(), bounds.top.toInt() + bounds.height().toInt())
    }
}