package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.Building
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
        canvas?.translate(-xOffset , -yOffset)

        canvas?.drawBitmap(background, 0f, 0f, paint)

        level.buildings.forEach {
            drawBuilding(it, canvas)
        }
    }

    fun drawBuilding(building : Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
    }

    fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paint.color = R.color.colorLeft

        canvas?.drawPath(building.leftSide, paint)
    }

    fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paint.color = R.color.colorCenter

        canvas?.drawPath(building.centerSide, paint)
    }

    fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paint.color = Color.WHITE

        canvas?.drawPath(building.roof, paint)
    }
}