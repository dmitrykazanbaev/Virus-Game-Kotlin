package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.view.View
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextSingleton


abstract class AbstractLevelView(context : Context) : View(context) {
    val background : Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    val paint = Paint()
    abstract val level : AbstractLevel

    init {
        ApplicationContextSingleton.instance?.initialize(context)
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawBitmap(background, 0f, 0f, paint)

        level.buildings.forEach {
            drawBuilding(it as JsonObject, canvas)
        }
    }

    fun drawBuilding(building : JsonObject, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
    }

    fun drawLeftSideBuilding(building: JsonObject, canvas: Canvas?) {
        paint.color = R.color.colorLeft

        var path : Path = Path()
        building.string("left")?.let {
            path = drawFigureWithPath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun drawCenterSideBuilding(building: JsonObject, canvas: Canvas?) {
        paint.color = R.color.colorCenter

        var path : Path = Path()
        building.string("center")?.let {
            path = drawFigureWithPath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun drawRoofBuilding(building: JsonObject, canvas: Canvas?) {
        paint.color = Color.WHITE

        var path : Path = Path()
        building.string("roof")?.let {
            path = drawFigureWithPath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun drawFigureWithPath(building: String): Path {
        val path : Path = Path()
        val coordinates = building.split(",")

        path.moveTo(coordinates[0].toFloat(), coordinates[1].toFloat())
        for (i in 2 until coordinates.size step 2) {
            path.lineTo(coordinates[i].toFloat(), coordinates[i + 1].toFloat())
        }
        path.close()

        return path
    }
}