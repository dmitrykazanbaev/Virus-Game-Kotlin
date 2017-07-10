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
                infectBuilding()
                delay(500)
            }
        }
    }

    private fun infectBuilding() {
        val randomBuilding = Random().nextInt(buildings.size)
        Log.w("dmka", "buildings[$randomBuilding].infectedComputers ${buildings[randomBuilding].infectedComputers}")
        if (!buildings[randomBuilding].isInfected) {
            buildings[randomBuilding].infectedComputers++
        }
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
            val leftSide = getFigurePath(string("left"))
            val centerSide = getFigurePath(string("center"))
            val roof = getFigurePath(string("roof"))

            return Building(leftSide, centerSide, roof)
        }
    }

    private fun getFigurePath(building: String?): Path {
        val path: Path = Path()

        if (building != null) {
            val coordinates = building.split(",")

            path.moveTo(coordinates[0].toFloat(), coordinates[1].toFloat())
            for (i in 2 until coordinates.size step 2) {
                path.lineTo(coordinates[i].toFloat(), coordinates[i + 1].toFloat())
            }
            path.close()
        }

        return path
    }
}