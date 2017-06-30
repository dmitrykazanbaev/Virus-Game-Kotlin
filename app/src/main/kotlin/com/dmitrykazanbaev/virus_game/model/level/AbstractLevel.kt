package com.dmitrykazanbaev.virus_game.model.level

import android.graphics.Path
import android.graphics.Point
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import java.io.InputStream


abstract class AbstractLevel {
    val applicationContext = ApplicationContextHolder.context

    var buildings = mutableListOf<Building>()

    var maxPoint = Point()

    abstract protected fun initializeLevelWithBuildings()

    protected fun getJsonBuildings(input: InputStream): JsonArray<*> {
        val parser: Parser = Parser()
        return parser.parse(input) as JsonArray<*>
    }

    protected fun getBuilding(jsonBuilding: JsonObject): Building {
        with(jsonBuilding) {
            val leftSide = getFigurePath(string("left"))
            val centerSide = getFigurePath(string("center"))
            val roof = getFigurePath(string("roof"))

            val building = Building(leftSide, centerSide, roof)
            maxPoint.x = maxOf(building.maxPoint.x, maxPoint.x)
            maxPoint.y = maxOf(building.maxPoint.y, maxPoint.y)

            return building
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