package com.dmitrykazanbaev.virus_game.custom_views

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


class ModificationButton
@JvmOverloads constructor(context: Context,
                          var startAngle: Float,
                          val sweepAngle: Float,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : Button(context, attrs, defStyleAttr) {

    val sectorPath = Path()
    val sectorPaint = Paint()

    val center by lazy { Point(width / 2, height / 2) }
    val outerRadius by lazy { minOf(width, height) / 2f }
    val innerRadius by lazy { outerRadius / 2 }

    val innerOval by lazy { getOvalForArc(center, innerRadius) }
    val outerOval by lazy { getOvalForArc(center, outerRadius) }

    val firstSeparatorOval by lazy {
        getOvalForArc(center, (outerRadius - innerRadius) / 3 + innerRadius) // 1/3 from inner
    }
    val secondSeparatorOval by lazy {
        getOvalForArc(center, (outerRadius - innerRadius) / 3 * 2 + innerRadius) // 2/3 from inner
    }
    val separatorPaint = Paint()

    val sectorSeparatorLine by lazy {
        val startX = center.x + innerRadius * Math.cos((startAngle + sweepAngle) * Math.PI / 180)
        val startY = center.y + innerRadius * Math.sin((startAngle + sweepAngle) * Math.PI / 180)
        val stopX = center.x + outerRadius * Math.cos((startAngle + sweepAngle) * Math.PI / 180)
        val stopY = center.y + outerRadius * Math.sin((startAngle + sweepAngle) * Math.PI / 180)

        mutableListOf(Point(startX.toInt(), startY.toInt()), Point(stopX.toInt(), stopY.toInt()))
    }
    val sectorSeparatorPaint = Paint()

    private fun getOvalForArc(center: Point, radius: Float) =
            RectF(center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius)

    init {
        this.startAngle -= 90f // to start from the top, not from the right

        sectorPaint.color = ContextCompat.getColor(ApplicationContextHolder.context, R.color.modification_button_color)
        sectorPaint.style = Paint.Style.FILL

        separatorPaint.color = Color.BLACK
        separatorPaint.style = Paint.Style.STROKE
        separatorPaint.strokeWidth = resources.getString(R.dimen.stroke_separator).toFloat()

        sectorSeparatorPaint.color = Color.BLACK
        sectorSeparatorPaint.strokeWidth = 6f
        sectorSeparatorPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        sectorPath.reset()
        sectorPath.arcTo(innerOval, startAngle, sweepAngle)
        sectorPath.arcTo(outerOval, startAngle + sweepAngle, -sweepAngle)
        sectorPath.close()

        canvas?.let {
            canvas.drawPath(sectorPath, sectorPaint)
            canvas.drawArc(firstSeparatorOval, startAngle, sweepAngle, false, separatorPaint)
            canvas.drawArc(secondSeparatorOval, startAngle, sweepAngle, false, separatorPaint)
            canvas.drawLine(sectorSeparatorLine[0].x.toFloat(),
                    sectorSeparatorLine[0].y.toFloat(),
                    sectorSeparatorLine[1].x.toFloat(),
                    sectorSeparatorLine[1].y.toFloat(), sectorSeparatorPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pathBounds = RectF()
        sectorPath.computeBounds(pathBounds, true)

        val buttonRegion = Region()
        buttonRegion.setPath(sectorPath, Region(pathBounds.left.toInt(), pathBounds.top.toInt(), pathBounds.right.toInt(), pathBounds.bottom.toInt()))

        if (buttonRegion.contains(event.x.toInt(), event.y.toInt()))
            return super.onTouchEvent(event)
        else
            return false
    }
}