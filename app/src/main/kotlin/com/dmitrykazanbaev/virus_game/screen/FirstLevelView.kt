package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel

class FirstLevelView(context: Context) : AbstractLevelView(context, FirstLevel()) {
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

    fun drawBuilding(building: Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
        drawInfectedRoofBuilding(building, canvas)
    }

    private fun drawInfectedRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorFillingRoof)

        canvas?.drawPath(building.infectedRoof, paintForFilling)
    }

    fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorLeft)

        canvas?.drawPath(building.leftSide, paintForFilling)
    }

    fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = ContextCompat.getColor(context, R.color.colorCenter)

        canvas?.drawPath(building.centerSide, paintForFilling)
        canvas?.drawPath(building.centerSide, paintForStroke)
    }

    fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = Color.WHITE

        canvas?.drawPath(building.roof, paintForFilling)
        canvas?.drawPath(building.roof, paintForStroke)
    }
}