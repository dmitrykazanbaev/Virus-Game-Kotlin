package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import com.beust.klaxon.JsonObject
import com.beust.klaxon.string
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextSingleton


abstract class AbstractLevelView(context : Context) : View(context) {
    protected val background : Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    protected val paint = Paint()
    protected val gestureDetector : GestureDetector = GestureDetector(context, MyGestureListener())

    var xOffset = 0f
    var yOffset = 0f

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            xOffset += distanceX
            if (xOffset < 0f) {
                xOffset = 0f
            }
//            if (xOffset > xBig) {
//                xOffset = xBig
//            }

            yOffset += distanceY
            if (yOffset < 0f) {
                yOffset = 0f
            }
//            if (yOffset > yBig) {
//                yOffset = yBig
//            }
            invalidate()
            return true
        }
    }

    abstract val level : AbstractLevel

    init {
        ApplicationContextSingleton.instance?.initialize(context)
        paint.style = Paint.Style.FILL
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(-xOffset , -yOffset)

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
            path = getFigurePath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun drawCenterSideBuilding(building: JsonObject, canvas: Canvas?) {
        paint.color = R.color.colorCenter

        var path : Path = Path()
        building.string("center")?.let {
            path = getFigurePath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun drawRoofBuilding(building: JsonObject, canvas: Canvas?) {
        paint.color = Color.WHITE

        var path : Path = Path()
        building.string("roof")?.let {
            path = getFigurePath(it)
        }
        canvas?.drawPath(path, paint)
    }

    fun getFigurePath(building: String): Path {
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