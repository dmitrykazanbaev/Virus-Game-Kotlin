package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.view.*
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.Building
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextSingleton


abstract class AbstractLevelView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    protected val background: Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    protected val paint = Paint()
    protected val scrollGestureDetector = GestureDetector(context, MyGestureListener())
    protected val scaleGestureDetector = ScaleGestureDetector(context, MyGestureListener())

    abstract val level: AbstractLevel

    private var drawThread: DrawThread? = null

    private var xOffset = 0f
    private var yOffset = 0f
    private var scaleFactor = 1f

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener(), ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(p0: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector?) {
        }

        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= detector?.scaleFactor!!
            return true
        }

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
            return true
        }
    }

    init {
        holder.addCallback(this)

        ApplicationContextSingleton.instance?.initialize(context)

        paint.style = Paint.Style.FILL
    }

    inner class DrawThread(private val surfaceHolder: SurfaceHolder) : Thread() {
        var runFlag = false

        val buildings = level.buildings

        override fun run() {
            var canvas: Canvas? = null
            while (runFlag) {
                try {
                    canvas = surfaceHolder.lockCanvas(null)
                    synchronized(surfaceHolder) {
                        draw(canvas)
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            }
        }

        fun draw(canvas: Canvas?) {
            canvas?.translate(-xOffset, -yOffset)
            canvas?.scale(scaleFactor, scaleFactor)

            canvas?.drawBitmap(background, 0f, 0f, paint)

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