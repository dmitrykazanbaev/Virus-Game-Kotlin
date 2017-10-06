package com.dmitrykazanbaev.virus_game.model.level

import android.content.Context
import android.graphics.Point
import com.dmitrykazanbaev.virus_game.model.dao.AbstractLevelDAO
import java.util.*

enum class Difficulty {
    EASY, MEDIUM, HARD
}

abstract class AbstractLevel(val applicationContext: Context, protected val jsonBuildingsResource: Int,
                             val difficulty: Difficulty = Difficulty.EASY) {

    abstract val phones: Int
    var infectedPhones = 0
        protected set
    var curedPhones = 0
        protected set

    var computers = 0
        protected set
    var curedComputers = 0
        protected set
    var infectedComputers = 0
        protected set

    var smartHome = 0
        protected set
    var curedSmartHome = 0
        protected set
    var infectedSmartHome = 0
        protected set

    abstract val profitComputer: Int
    abstract val profitPhone: Int
    abstract val profitSmartHome: Int

    abstract var levelCoefficient: Float

    var detectedDevices = 0
    abstract val countDetectedDevicesForStartAntivirusDevelopment: Int

    var antivirusProgress = 0

    abstract val countPhonesToCure: Int
    abstract val countComputersToCure: Int
    abstract val countSmartHomeToCure: Int

    val random = Random()

    var maxPoint = Point()
    var minPoint = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    var width = 0
        get() = maxPoint.x - minPoint.x
    var height = 0
        get() = maxPoint.y - minPoint.y

    var centerX: Int = 0
        get() = minPoint.x + (maxPoint.x - minPoint.x) / 2
    var centerY: Int = 0
        get() = minPoint.y + (maxPoint.y - minPoint.y) / 2

    abstract fun constructLevel()

    abstract fun getLevelState(): AbstractLevelDAO

    abstract fun setLevelState(levelState: AbstractLevelDAO)

    open fun infectPhone() {
        infectedPhones++
    }

    open fun infectComputer() {
        infectedComputers++
    }

    open fun infectSmartHome() {
        infectedSmartHome++
    }

    open fun curePhone() {
        if (infectedPhones >= countPhonesToCure) {

            infectedPhones -= countPhonesToCure
            curedPhones += countPhonesToCure
        } else {

            curedPhones += infectedPhones
            infectedPhones -= infectedPhones
        }
    }

    open fun cureComputer() {
        if (infectedComputers >= countComputersToCure) {

            infectedComputers -= countComputersToCure
            curedComputers += countComputersToCure
        } else {

            curedComputers += infectedComputers
            infectedComputers -= infectedComputers
        }
    }

    open fun cureSmartHome() {
        if (infectedSmartHome >= countSmartHomeToCure) {

            infectedSmartHome -= countSmartHomeToCure
            curedSmartHome += countSmartHomeToCure
        } else {

            curedSmartHome += infectedSmartHome
            infectedSmartHome -= infectedSmartHome
        }
    }
}