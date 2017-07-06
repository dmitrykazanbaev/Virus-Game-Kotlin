package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.view.*
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking


abstract class AbstractLevelView(context: Context, protected val level: AbstractLevel) : SurfaceView(context), SurfaceHolder.Callback {
    protected val background: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    protected val paintForFilling = Paint()
    protected val paintForStroke = Paint()
    protected val scrollGestureDetector = GestureDetector(context, MyGestureListener())
    protected val scaleGestureDetector = ScaleGestureDetector(context, MyGestureListener())

    private var xOffset = 0f
    private var yOffset = 0f
    private var scaleFactor = 1f

    private var minScaleFactor = scaleFactor
    private var maxScaleFactor = scaleFactor

    private lateinit var drawJob: Job

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener(), ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector?) {
        }

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

    init {
        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        drawJob.cancel()
        runBlocking { drawJob.join() }
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        scaleFactor = minOf(width.toFloat() / level.width, height.toFloat() / level.height)
        scaleFactor *= 0.9f
        minScaleFactor = scaleFactor
        maxScaleFactor = 3 * minScaleFactor

        drawJob = launch(CommonPool) {
            var canvas: Canvas?
            while (isActive) {
                canvas = holder.lockCanvas()

                try {
                    synchronized(holder) {
                        canvas?.let { drawLevel(it) }
                    }
                } finally {
                    canvas?.let { holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    fun drawLevel(canvas: Canvas) {
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.translate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        //canvas.drawBitmap(background, 0f, 0f, paintForFilling)
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorBackground))

        level.buildings.forEach {
            drawBuilding(it, canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return scrollGestureDetector.onTouchEvent(event) && scaleGestureDetector.onTouchEvent(event)
    }

    fun drawBuilding(building: Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
    }

    fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = Color.BLACK

        canvas?.drawPath(building.leftSide, paintForFilling)
    }

    fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = R.color.colorCenter

        canvas?.drawPath(building.centerSide, paintForFilling)
        canvas?.drawPath(building.centerSide, paintForStroke)
    }

    fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = Color.WHITE

        canvas?.drawPath(building.roof, paintForFilling)
        canvas?.drawPath(building.roof, paintForStroke)
    }
}