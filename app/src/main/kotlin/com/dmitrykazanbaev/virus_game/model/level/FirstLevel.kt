package com.dmitrykazanbaev.virus_game.model.level

import android.graphics.Point
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.dao.AbstractLevelDAO
import com.dmitrykazanbaev.virus_game.model.dao.BuildingDAO
import com.dmitrykazanbaev.virus_game.model.dao.FirstLevelDAO
import com.dmitrykazanbaev.virus_game.service.getFigurePath
import com.dmitrykazanbaev.virus_game.service.getPointsListFromJsonString
import com.dmitrykazanbaev.virus_game.service.getSortedPointsByClockwiseFromLeft
import java.io.InputStream
import java.util.*

class FirstLevel : AbstractLevel(R.raw.house_fin) {
    var buildings = mutableListOf<Building>()

    var maxPoint = Point()
    var minPoint = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    override var width = 0
        get() = maxPoint.x - minPoint.x
    override var height = 0
        get() = maxPoint.y - minPoint.y

    init {
        constructLevel()

        setMinMaxPoints()
    }

    override fun infect() {
        val randomBuilding = Random().nextInt(buildings.size)
        if (buildings[randomBuilding].canInfectComputer) {
            buildings[randomBuilding].infectedComputers++
        }
    }

    override fun constructLevel() {
        val jsonBuildings = getJsonBuildings(applicationContext.resources.openRawResource(jsonBuildingsResource))

        jsonBuildings.forEach {
            buildings.add(getBuilding(it as JsonObject))
        }
    }

    override fun getLevelState(): FirstLevelDAO {
        val firstLevelDAO = FirstLevelDAO()

        buildings.map { BuildingDAO(it.infectedComputers) }
                .forEach { firstLevelDAO.buildingList.add(it) }

        return firstLevelDAO
    }

    override fun setLevelState(levelState: AbstractLevelDAO) {
        (levelState as FirstLevelDAO).buildingList.withIndex().
                forEach { (index, value) ->
                    buildings[index].infectedComputers = value.infectedComputers
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


    private fun getJsonBuildings(input: InputStream): JsonArray<*> {
        val parser = Parser()
        return parser.parse(input) as JsonArray<*>
    }

    private fun getBuilding(jsonBuilding: JsonObject): Building {
        with(jsonBuilding) {
            val leftSidePoints = //getSortedPointsByClockwiseFromLeft(
                    getPointsListFromJsonString(string("left"))//)
            val centerSidePoints = //getSortedPointsByClockwiseFromLeft(
                    getPointsListFromJsonString(string("center"))//)
            val roofPoints = getSortedPointsByClockwiseFromLeft(
                    getPointsListFromJsonString(string("roof")))

            val leftSide = getFigurePath(leftSidePoints)
            val centerSide = getFigurePath(centerSidePoints)
            val roof = getFigurePath(roofPoints)

            return Building(leftSide, centerSide, roof, leftSidePoints, centerSidePoints, roofPoints)
        }
    }
}