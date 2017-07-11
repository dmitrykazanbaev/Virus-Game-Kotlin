package com.dmitrykazanbaev.virus_game.model.level

import android.graphics.Path
import android.graphics.Point
import android.util.Log
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.io.InputStream
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


abstract class AbstractLevel(private val JsonBuildingsResource: Int) {
    val applicationContext = ApplicationContextHolder.context

    var buildings = mutableListOf<Building>()

    var maxPoint = Point()
    var minPoint = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    var width = 0
        get() = maxPoint.x - minPoint.x
    var height = 0
        get() = maxPoint.y - minPoint.y

    init {
        initializeLevelWithBuildings()
        setMinMaxPoints()

        launch(CommonPool) {
            while (isActive) {
                if (infectBuilding())
                    delay(500)
            }
        }
    }

    private fun infectBuilding(): Boolean {
        val randomBuilding = Random().nextInt(buildings.size)
//        val a = 6
        if (!buildings[randomBuilding].isInfected) {
            buildings[randomBuilding].infectedComputers++
//            Log.w("dmka center", "${buildings[randomBuilding].centerSidePoints}")
            return true
        }

        return false
    }

    private fun setMinMaxPoints() {
        buildings.forEach {
            maxPoint.x = maxOf(it.maxPoint.x, maxPoint.x)
            maxPoint.y = maxOf(it.maxPoint.y, maxPoint.y)

            minPoint.x = minOf(it.minPoint.x, minPoint.x)
            minPoint.y = minOf(it.minPoint.y, minPoint.y)
        }
    }

    fun initializeLevelWithBuildings() {
        val jsonBuildings = getJsonBuildings(applicationContext.resources.openRawResource(JsonBuildingsResource))

        jsonBuildings.forEach {
            buildings.add(getBuilding(it as JsonObject))
        }
    }

    protected fun getJsonBuildings(input: InputStream): JsonArray<*> {
        val parser: Parser = Parser()
        return parser.parse(input) as JsonArray<*>
    }

    protected fun getBuilding(jsonBuilding: JsonObject): Building {
        with(jsonBuilding) {
            val leftSidePoints = //getSortedPointsByClockwiseFromLeft(
                    getPointsList(string("left"))//)
            val centerSidePoints = //getSortedPointsByClockwiseFromLeft(
                    getPointsList(string("center"))//)
            val roofPoints = getSortedPointsByClockwiseFromLeft(
                    getPointsList(string("roof")))

            val leftSide = getFigurePath(leftSidePoints)
            val centerSide = getFigurePath(centerSidePoints)
            val roof = getFigurePath(roofPoints)

            return Building(leftSide, centerSide, roof, leftSidePoints, centerSidePoints, roofPoints)
        }
    }

    private fun getSortedPointsByClockwiseFromLeft(list: List<Point>): List<Point> {
        if (list.isNotEmpty()) {
            val leftPoint = list.minWith(compareBy(Point::x).thenBy(Point::y))

            return list.sortedWith(comparePointsByClockwiseFrom(leftPoint!!))
        }

        return list
    }

    inner class comparePointsByClockwiseFrom(val center: Point) : Comparator<Point> {
        override fun compare(p0: Point, p1: Point): Int {
            if (p0.x - center.x >= 0 && p1.x - center.x < 0) {
                return 1
            }
            if (p0.x - center.x < 0 && p1.x - center.x >= 0) {
                return -1
            }

            if (p0.x - center.x == 0 && p1.x - center.x == 0) {
                if (p0.y - center.y >= 0 || p1.y - center.y >= 0) {
                    return p0.y.compareTo(p1.y)
                }
                return p1.y.compareTo(p0.y)
            }

            // compute the cross product of vectors (center -> a) x (center -> b)
            val det = (p0.x - center.x) * (p1.y - center.y) - (p1.x - center.x) * (p0.y - center.y)
            if (det < 0) {
                return 1
            }
            if (det > 0) {
                return -1
            }

            // points a and b are on the same line from the center
            // check which point is closer to the center
            val d1 = (p0.x - center.x) * (p0.x - center.x) + (p0.y - center.y) * (p0.y - center.y)
            val d2 = (p1.x - center.x) * (p1.x - center.x) + (p1.y - center.y) * (p1.y - center.y)
            return d1.compareTo(d2)
        }
    }

    private fun getPointsList(building: String?): List<Point> {
        val list = ArrayList<Point>()

        if (building != null) {
            val coordinates = building.split(",")

            (0 until coordinates.size step 2).mapTo(list) { Point(coordinates[it].toInt(), coordinates[it + 1].toInt()) }
        }

        return list
    }

    private fun getFigurePath(points: List<Point>): Path {
        val path: Path = Path()

        if (points.isNotEmpty()) {
            path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
            for (i in 1 until points.size) {
                path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
            }
            path.close()
        }

        return path
    }
}