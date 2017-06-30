package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


abstract class AbstractLevelView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    protected val background: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    protected val paintForFilling = Paint()
    protected val paintForStroke = Paint()
    protected val scrollGestureDetector = GestureDetector(context, MyGestureListener())
    protected val scaleGestureDetector = ScaleGestureDetector(context, MyGestureListener())

    abstract val level: AbstractLevel

    private var drawThread: DrawThread? = null

    private var xOffset = 0f
    private var yOffset = 0f
    private var scaleFactor = 1f

    private var maxPoint = Point()
    private var minScaleFactor = scaleFactor
    private var maxScaleFactor = scaleFactor

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
            //Log.w("dmka", "${e?.x} ${e?.y}")
            return true
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            xOffset += distanceX
            if (xOffset < 0f) {
                xOffset = 0f
            }
            if (xOffset > maxPoint.x) {
                xOffset = maxPoint.x.toFloat()
            }

            yOffset += distanceY
            if (yOffset < 0f) {
                yOffset = 0f
            }
            if (yOffset > maxPoint.y) {
                yOffset = maxPoint.y.toFloat()
            }

            return true
        }
    }

    init {
        holder.addCallback(this)

        ApplicationContextHolder.context = context

        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK
    }

    inner class DrawThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        var runFlag = false

        val buildings = level.buildings

        override fun run() {
            var canvas: Canvas?
            while (runFlag) {
                canvas = surfaceHolder.lockCanvas()

                synchronized(surfaceHolder) {
                    canvas?.let {
                        draw(it)
                    }
                }

                canvas?.let {
                    surfaceHolder.unlockCanvasAndPost(it)
                }
            }
        }

        fun draw(canvas: Canvas) {
            canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
            canvas.translate(-xOffset/scaleFactor, -yOffset/scaleFactor)

            //canvas.drawBitmap(background, 0f, 0f, paintForFilling)
            canvas.drawColor(ContextCompat.getColor(context, R.color.colorBackground))

            buildings.forEach {
                drawBuilding(it, canvas)
            }
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        var retry = true
        drawThread?.runFlag = false
        while (retry) {
            try {
                drawThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                throw Exception("DrawThread stopping problem")
            }
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        maxPoint = Point(level.maxPoint.x - (width * 0.9).toInt(), level.maxPoint.y - (height * 0.9).toInt())

        scaleFactor = minOf(width.toFloat() / level.maxPoint.x, height.toFloat() / level.maxPoint.y)
        scaleFactor *= 0.9f
        minScaleFactor = scaleFactor
        maxScaleFactor = 3 * minScaleFactor

        //TODO
        //xOffset = -1.2f*(width / 2 - (level.maxPoint.x - level.minPoint.x) / 2) * scaleFactor
        //yOffset = -1.2f*(height / 2 - (level.maxPoint.y - level.minPoint.y) / 2) * scaleFactor
//        Log.w("dmka", "$xOffset $yOffset")

        drawThread = DrawThread(holder)
        drawThread?.runFlag = true
        drawThread?.start()
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