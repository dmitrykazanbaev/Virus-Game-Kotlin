package com.dmitrykazanbaev.virus_game.model

import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import kotlin.properties.Delegates

data class Building(val leftSide: Path, val centerSide: Path, val roof: Path,
                    val leftSidePoints: List<Point>, val centerSidePoints: List<Point>, val roofPoints: List<Point>) {
    val maxPoint: Point
    val minPoint: Point
    var infectedRoof = Path()
    var isInfected = false

    var computers = 5
    var infectedComputers by Delegates.observable(0) {
        _, oldValue, newValue ->
        if (oldValue == newValue - 1) {
            computeInfectedRoof()
            if (newValue == computers)
                isInfected = true
        }
    }

    init {
        val bounds = RectF()
        roof.computeBounds(bounds, false)
        minPoint = Point(bounds.left.toInt(), bounds.top.toInt())
        maxPoint = Point(bounds.left.toInt() + bounds.width().toInt(), bounds.top.toInt() + bounds.height().toInt())
    }

    private fun computeInfectedRoof() {
        if (infectedComputers > 0) {
            infectedRoof.reset()

            infectedRoof.moveTo(roofPoints[0].x.toFloat(), roofPoints[0].y.toFloat())

            infectedRoof.lineTo((roofPoints[1].x - roofPoints[0].x) * infectedComputers / computers + roofPoints[0].x.toFloat(),
                    (roofPoints[1].y - roofPoints[0].y) * infectedComputers / computers + roofPoints[0].y.toFloat())

            infectedRoof.lineTo((roofPoints[2].x - roofPoints[3].x) * infectedComputers / computers + roofPoints[3].x.toFloat(),
                    (roofPoints[2].y - roofPoints[3].y) * infectedComputers / computers + roofPoints[3].y.toFloat())

            infectedRoof.lineTo(roofPoints[3].x.toFloat(), roofPoints[3].y.toFloat())

            infectedRoof.close()
        }
    }
}