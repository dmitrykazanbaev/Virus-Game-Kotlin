package com.dmitrykazanbaev.virus_game.model.level

import android.graphics.Point
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
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
import java.util.concurrent.ConcurrentLinkedQueue

class FirstLevel : AbstractLevel(R.raw.house_fin) {
    var buildings = mutableListOf<Building>()

    val phones = 600
    var infectedPhones = 0
    var curedPhones = 0

    var computers = 0
        private set
    var curedComputers = 0
        private set
    var infectedComputers = 0
        private set

    var smartHome = 0
        private set
    var curedSmartHome = 0
        private set
    var infectedSmartHome = 0
        private set

    val profitComputer = 20
    val profitPhone = 20
    val profitSmartHome = 20

    var levelCoefficient = 0.5

    var detectedDevices = 0
    val countDetectedDevicesForStartAntivirusDevelopment = 450

    var antivirusProgress = 0

    private val countPhonesToCure = 20
    private val countComputersToCure = 12
    private val countSmartHomeToCure = 8

    val random = Random()
    val infectedPhonesToDraw = ConcurrentLinkedQueue<InfectedPhone>()

    inner class InfectedPhoneThread(name: String) : HandlerThread(name) {
        val ADD_PHONE = 0
        val REMOVE_PHONE = 1
        lateinit var mHandler: Handler

        fun prepareHandler() {
            mHandler = object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        ADD_PHONE -> {
                            val infectedPhone = InfectedPhone(
                                    center = Point(random.nextInt(maxPoint.x - minPoint.x) + minPoint.x,
                                            random.nextInt(maxPoint.y - minPoint.y) + minPoint.y),
                                    outerRadius = height / 100f,
                                    minX = minPoint.x, minY = minPoint.y,
                                    maxX = maxPoint.x, maxY = maxPoint.y)
                            infectedPhonesToDraw.add(infectedPhone)
                            infectedPhone.animation.start()
                        }
                        REMOVE_PHONE -> {
                            infectedPhonesToDraw.last().animation.end()
                            infectedPhonesToDraw.remove()
                        }
                    }
                }
            }
        }
    }

    val infectedPhoneThread = InfectedPhoneThread("InfectedPhoneThread")
    private val percentInfectedPhoneToDraw = 4

    var maxPoint = Point()
    var minPoint = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    override var width = 0
        get() = maxPoint.x - minPoint.x
    override var height = 0
        get() = maxPoint.y - minPoint.y

    override var centerX: Int = 0
        get() = minPoint.x + (maxPoint.x - minPoint.x) / 2
    override var centerY: Int = 0
        get() = minPoint.y + (maxPoint.y - minPoint.y) / 2

    init {
        constructLevel()

        setMinMaxPoints()

        infectedPhoneThread.start()
        infectedPhoneThread.prepareHandler()
    }

    override fun constructLevel() {
        val jsonBuildings = getJsonBuildings(applicationContext.resources.openRawResource(jsonBuildingsResource))

        jsonBuildings.forEach {
            val building = getBuilding(it as JsonObject)
            computers += building.computers
            smartHome += building.smartHome

            buildings.add(building)
        }
    }

    override fun getLevelState(): FirstLevelDAO {
        val firstLevelDAO = FirstLevelDAO()

        firstLevelDAO.antivirusProgress = antivirusProgress
        firstLevelDAO.infectedPhones = infectedPhones
        firstLevelDAO.curedPhones = curedPhones
        firstLevelDAO.detectedDevices = detectedDevices

        buildings.map { BuildingDAO(it.infectedComputers, it.infectedSmartHome, it.curedComputers, it.curedSmartHome) }
                .forEach { firstLevelDAO.buildingList.add(it) }

        return firstLevelDAO
    }

    override fun setLevelState(levelState: AbstractLevelDAO) {
        (levelState as FirstLevelDAO).buildingList.withIndex().
                forEach { (index, value) ->
                    buildings[index].infectedComputers = value.infectedComputers
                    buildings[index].infectedSmartHome = value.infectedSmartHome
                    buildings[index].curedComputers = value.curedComputers
                    buildings[index].curedSmartHome = value.curedSmartHome

                    infectedComputers += buildings[index].infectedComputers
                    infectedSmartHome += buildings[index].infectedSmartHome
                    curedComputers += buildings[index].curedComputers
                    curedSmartHome += buildings[index].curedSmartHome
                }

        antivirusProgress = levelState.antivirusProgress
        infectedPhones = levelState.infectedPhones
        curedPhones = levelState.curedPhones
        detectedDevices = levelState.detectedDevices

        synchronizeInfectedPhoneToDraw()
    }

    fun infectPhone() {
        infectedPhones++
        if (infectedPhones % percentInfectedPhoneToDraw == 1) addInfectedPhoneToDrawing()
    }

    fun infectComputer() {
        val filteredBuildings = buildings.filter { it.canInfectComputer }
        if (filteredBuildings.isNotEmpty()) {
            val randomBuilding = Random().nextInt(filteredBuildings.size)
            filteredBuildings[randomBuilding].infectedComputers++
            infectedComputers++
        }
    }

    fun infectSmartHome() {
        val filteredBuildings = buildings.filter { it.canInfectSmartHome }
        if (filteredBuildings.isNotEmpty()) {
            val randomBuilding = Random().nextInt(filteredBuildings.size)
            filteredBuildings[randomBuilding].infectedSmartHome++
            infectedSmartHome++
        }
    }

    fun curePhone() {
        if (infectedPhones >= countPhonesToCure) {
            infectedPhones -= countPhonesToCure
            curedPhones += countPhonesToCure

            removeInfectedPhoneFromDrawing(countPhonesToCure / percentInfectedPhoneToDraw)
        } else {
            curedPhones += infectedPhones
            infectedPhones -= infectedPhones

            removeInfectedPhoneFromDrawing(infectedPhonesToDraw.size)
        }
    }

    fun cureComputer() {
        var computersToCure = countComputersToCure
        val filteredBuildings = buildings.filter { it.canCureComputer } as ArrayList

        while (computersToCure > 0 && filteredBuildings.isNotEmpty()) {
            val randomBuilding = Random().nextInt(filteredBuildings.size)

            if (filteredBuildings[randomBuilding].infectedComputers >= computersToCure) {
                infectedComputers -= computersToCure
                curedComputers += computersToCure

                filteredBuildings[randomBuilding].infectedComputers -= computersToCure
                filteredBuildings[randomBuilding].curedComputers += computersToCure

                break
            } else {
                infectedComputers -= filteredBuildings[randomBuilding].infectedComputers
                curedComputers += filteredBuildings[randomBuilding].infectedComputers

                computersToCure -= filteredBuildings[randomBuilding].infectedComputers

                filteredBuildings[randomBuilding].curedComputers += filteredBuildings[randomBuilding].infectedComputers
                filteredBuildings[randomBuilding].infectedComputers -= filteredBuildings[randomBuilding].infectedComputers

                filteredBuildings.removeAt(randomBuilding)
            }
        }
    }

    fun cureSmartHome() {
        var smartHomeToCure = countSmartHomeToCure
        val filteredBuildings = buildings.filter { it.canCureSmartHome } as ArrayList

        while (smartHomeToCure > 0 && filteredBuildings.isNotEmpty()) {
            val randomBuilding = Random().nextInt(filteredBuildings.size)

            if (filteredBuildings[randomBuilding].infectedSmartHome >= smartHomeToCure) {
                infectedSmartHome -= smartHomeToCure
                curedSmartHome += smartHomeToCure

                filteredBuildings[randomBuilding].infectedSmartHome -= smartHomeToCure
                filteredBuildings[randomBuilding].curedSmartHome += smartHomeToCure

                break
            } else {
                infectedSmartHome -= filteredBuildings[randomBuilding].infectedSmartHome
                curedSmartHome += filteredBuildings[randomBuilding].infectedSmartHome

                smartHomeToCure -= filteredBuildings[randomBuilding].infectedSmartHome

                filteredBuildings[randomBuilding].curedSmartHome += filteredBuildings[randomBuilding].infectedSmartHome
                filteredBuildings[randomBuilding].infectedSmartHome -= filteredBuildings[randomBuilding].infectedSmartHome

                filteredBuildings.removeAt(randomBuilding)
            }
        }
    }

    private fun addInfectedPhoneToDrawing(count: Int = 1) {
        repeat(count) {
            infectedPhoneThread.mHandler.sendEmptyMessage(infectedPhoneThread.ADD_PHONE)
        }
    }

    private fun removeInfectedPhoneFromDrawing(count: Int = 1) {
        repeat(count) {
            if (infectedPhonesToDraw.isNotEmpty()) {
                infectedPhoneThread.mHandler.sendEmptyMessage(infectedPhoneThread.REMOVE_PHONE)
            }
        }
    }

    private fun synchronizeInfectedPhoneToDraw() {
        addInfectedPhoneToDrawing(infectedPhones / percentInfectedPhoneToDraw)
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