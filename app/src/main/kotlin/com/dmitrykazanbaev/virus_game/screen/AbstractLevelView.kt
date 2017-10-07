package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Point
import android.view.*
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import io.realm.Realm


abstract class AbstractLevelView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    abstract val level: AbstractLevel
    protected val realm = Realm.getDefaultInstance()!!

    private val scrollGestureDetector = GestureDetector(context, MyGestureListener())
    private val scaleGestureDetector = ScaleGestureDetector(context, MyGestureListener())

    protected var xOffset = 0f
    protected var yOffset = 0f

    protected var scaleFactor = 1f
    protected var minScaleFactor = scaleFactor
    protected var maxScaleFactor = scaleFactor

    protected fun translateXYToLocalCoordinates(x: Float, y: Float): Point {
        val mClickCoords = FloatArray(2)

        mClickCoords[0] = x
        mClickCoords[1] = y

        val matrix = Matrix()
        matrix.set(getMatrix())

        matrix.preScale(scaleFactor, scaleFactor, level.centerX.toFloat(), level.centerY.toFloat())
        matrix.preTranslate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        matrix.invert(matrix)

        matrix.mapPoints(mClickCoords)

        return Point(mClickCoords[0].toInt(), mClickCoords[1].toInt())
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener(), ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector?) {}

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector?.scaleFactor!!
            if (scaleFactor < minScaleFactor)
                scaleFactor = minScaleFactor
            if (scaleFactor > maxScaleFactor)
                scaleFactor = maxScaleFactor

            return true
        }

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            xOffset += distanceX
            /*if (level.width * scaleFactor > width) {
                if (xOffset < level.minPoint.x) {
                    xOffset = level.minPoint.x.toFloat()
                } else if (xOffset > level.minPoint.x + (level.width - width)) {
                    xOffset = ((level.minPoint.x + (level.width - width)).toFloat())
                }
            } else {
                if (xOffset > level.minPoint.x) {
                    xOffset = level.minPoint.x.toFloat()
                } else if (xOffset < level.minPoint.x + (level.width - width)) {
                    xOffset = ((level.minPoint.x + (level.width - width)).toFloat())
                }
            }*/

            yOffset += distanceY
            /*if (level.height * scaleFactor > height) {
                if (yOffset < level.minPoint.y) {
                    yOffset = level.minPoint.y.toFloat()
                } else if (yOffset > level.minPoint.y + (level.height - height)) {
                    yOffset = ((level.minPoint.y + (level.height - height)).toFloat())
                }
            } else {
                if (yOffset > level.minPoint.y) {
                    yOffset = level.minPoint.y.toFloat()
                } else if (yOffset < level.minPoint.y + (level.height - height)) {
                    yOffset = ((level.minPoint.y + (level.height - height)).toFloat())
                }
            }*/

            return true
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {}

    override fun surfaceDestroyed(p0: SurfaceHolder?) {}

    override fun surfaceCreated(p0: SurfaceHolder?) {
        scaleFactor = minOf(width.toFloat() / level.width, height.toFloat() / level.height)
        scaleFactor *= 0.8f
        minScaleFactor = scaleFactor
        maxScaleFactor = 3 * minScaleFactor
    }

    abstract fun drawLevel(canvas: Canvas)

    abstract fun saveLevelToRealm()

    abstract fun initLevelFromRealm()

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return scrollGestureDetector.onTouchEvent(event) && scaleGestureDetector.onTouchEvent(event)
    }
}