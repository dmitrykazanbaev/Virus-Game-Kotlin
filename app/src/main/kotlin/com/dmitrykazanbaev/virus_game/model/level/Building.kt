package com.dmitrykazanbaev.virus_game.model.level

import android.graphics.Path
import android.graphics.Point
import android.graphics.RectF
import kotlin.properties.Delegates

data class Building(val leftSide: Path, val centerSide: Path, val roof: Path,
                    val leftSidePoints: List<Point>, val centerSidePoints: List<Point>, val roofPoints: List<Point>) {
    val maxPoint: Point
    val minPoint: Point
    var infectedRoof = Path()

    var canInfectComputer = true
        get() = infectedComputers + curedComputers < computers

    var canCureComputer = false
        get() = infectedComputers > 0

    var canInfectSmartHome = true
        get() = infectedSmartHome + curedSmartHome < smartHome

    var canCureSmartHome = false
        get() = infectedSmartHome > 0

    var computers = 6
    var curedComputers = 0
    var infectedComputers by Delegates.observable(0) { _, _, _ -> computeInfectedRoof() }

    var smartHome = 4
    var curedSmartHome = 0
    var infectedSmartHome by Delegates.observable(0) { _, _, _ -> computeInfectedRoof() }

    val centerForMessage: Point

    var hasTechWorks = false

    init {
        val bounds = RectF()
        roof.computeBounds(bounds, false)
        minPoint = Point(bounds.left.toInt(), bounds.top.toInt())

        centerSide.computeBounds(bounds, false)
        maxPoint = Point(bounds.right.toInt(), bounds.bottom.toInt())
        centerForMessage = Point((bounds.left + (bounds.right - bounds.left) / 2).toInt(),
                (bounds.top + (bounds.bottom - bounds.top) / 2).toInt())
    }

    fun addTechWork() {
        hasTechWorks = true
    }

    private fun computeInfectedRoof() {
        infectedRoof.reset()

        if (infectedComputers in 1..computers) {
            infectedRoof.moveTo(roofPoints[0].x.toFloat(), roofPoints[0].y.toFloat())

            infectedRoof.lineTo((roofPoints[1].x - roofPoints[0].x) * getInfectedPercent() + roofPoints[0].x.toFloat(),
                    (roofPoints[1].y - roofPoints[0].y) * getInfectedPercent() + roofPoints[0].y.toFloat())

            infectedRoof.lineTo((roofPoints[2].x - roofPoints[3].x) * getInfectedPercent() + roofPoints[3].x.toFloat(),
                    (roofPoints[2].y - roofPoints[3].y) * getInfectedPercent() + roofPoints[3].y.toFloat())

            infectedRoof.lineTo(roofPoints[3].x.toFloat(), roofPoints[3].y.toFloat())

            infectedRoof.close()
        }
    }

    private fun getInfectedPercent() = (infectedComputers + infectedSmartHome).toFloat() / (computers + smartHome)

}