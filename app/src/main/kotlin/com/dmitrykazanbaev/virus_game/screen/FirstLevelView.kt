package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.dao.FirstLevelDAO
import com.dmitrykazanbaev.virus_game.model.level.Building
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import io.realm.Realm

class FirstLevelView(context: Context) : AbstractLevelView(context, FirstLevel()) {
    private val realm = Realm.getDefaultInstance()!!

    private val paintForFilling = Paint()
    private val paintForStroke = Paint()

    private val colorLeft = ContextCompat.getColor(context, R.color.colorLeft)
    private val colorCenter = ContextCompat.getColor(context, R.color.colorCenter)
    private val colorRoof = ContextCompat.getColor(context, R.color.colorRoof)
    private val colorInfectedRoof = ContextCompat.getColor(context, R.color.colorInfectedRoof)
    private val colorBackground = ContextCompat.getColor(context, R.color.colorBackground)

    init {
        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.isAntiAlias = true
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK
    }

    override fun drawLevel(canvas: Canvas) {
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.translate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        canvas.drawColor(colorBackground)

        (level as FirstLevel).buildings.forEach {
            drawBuilding(it, canvas)
        }
    }

    override fun saveLevelToRealm() {
        realm.executeTransaction {
            it.where(FirstLevelDAO::class.java).findAll().deleteAllFromRealm()

            val firstLevelDAO = level.getLevelState()

            it.copyToRealm(firstLevelDAO as FirstLevelDAO)
        }
    }

    override fun initLevelFromRealm() {
        val firstLevelDAO = realm.where(FirstLevelDAO::class.java).findFirst()

        firstLevelDAO?.let { level.setLevelState(it) }
    }

    fun drawBuilding(building: Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
        drawInfectedRoofBuilding(building, canvas)

        canvas?.drawPath(building.roof, paintForStroke)
    }

    private fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorLeft

        canvas?.drawPath(building.leftSide, paintForFilling)
        canvas?.drawPath(building.leftSide, paintForStroke)
    }

    private fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorCenter

        canvas?.drawPath(building.centerSide, paintForFilling)
        canvas?.drawPath(building.centerSide, paintForStroke)
    }

    private fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorRoof

        canvas?.drawPath(building.roof, paintForFilling)
    }

    private fun drawInfectedRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorInfectedRoof

        canvas?.drawPath(building.infectedRoof, paintForFilling)
    }
}