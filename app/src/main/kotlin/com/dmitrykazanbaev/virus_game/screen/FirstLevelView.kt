package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.dao.BuildingDAO
import com.dmitrykazanbaev.virus_game.model.dao.FirstLevelDAO
import com.dmitrykazanbaev.virus_game.model.level.Building
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import io.realm.Realm

class FirstLevelView(context: Context) : AbstractLevelView(context, FirstLevel()) {
    val realm = Realm.getDefaultInstance()!!

    private val paintForFilling = Paint()
    private val paintForStroke = Paint()

    init {
        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK
    }

    override fun drawLevel(canvas: Canvas) {
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.translate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        canvas.drawColor(ContextCompat.getColor(context, R.color.colorBackground))

        (level as FirstLevel).buildings.forEach {
            drawBuilding(it, canvas)
        }
    }

    override fun saveLevelToRealm() {
        realm.executeTransaction {
            it.where(FirstLevelDAO::class.java).findAll().deleteAllFromRealm()

            val firstLevelDAO = it.createObject(FirstLevelDAO::class.java)

            (level as FirstLevel).buildings
                    .map { BuildingDAO(it.infectedComputers) }
                    .forEach { firstLevelDAO.buildingList.add(it) }
        }
    }

    override fun initLevelFromRealm() {
        val firstLevelDAO = realm.where(FirstLevelDAO::class.java).findFirst()

        firstLevelDAO?.buildingList?.withIndex()?.
                forEach { (index, value) ->
                    (level as FirstLevel).
                            buildings[index].
                            infectedComputers = value.infectedComputers
                }

    }

    fun drawBuilding(building: Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
        drawInfectedRoofBuilding(building, canvas)
    }

    private fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorLeft)

        canvas?.drawPath(building.leftSide, paintForFilling)
    }

    private fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorCenter)

        canvas?.drawPath(building.centerSide, paintForFilling)
        canvas?.drawPath(building.centerSide, paintForStroke)
    }

    private fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorRoof)

        canvas?.drawPath(building.roof, paintForFilling)
        canvas?.drawPath(building.roof, paintForStroke)
    }

    private fun drawInfectedRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorInfectedRoof)

        canvas?.drawPath(building.infectedRoof, paintForFilling)
    }
}