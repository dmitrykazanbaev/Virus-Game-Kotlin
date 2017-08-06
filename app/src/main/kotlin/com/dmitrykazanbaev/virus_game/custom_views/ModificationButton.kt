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

    val center by lazy { Point(height / 2, height / 2) }
    val outerRadius by lazy { height / 2f }
    val innerRadius by lazy { outerRadius / 2 }

    var innerOval = RectF()
        get() = getOvalForArc(center, innerRadius)
    var outerOval = RectF()
        get() = getOvalForArc(center, outerRadius)

    var firstSeparatorOval = RectF()
        get() = getOvalForArc(center, (outerRadius - innerRadius) / 3 + innerRadius) // 1/3 from inner

    var secondSeparatorOval = RectF()
        get() = getOvalForArc(center, (outerRadius - innerRadius) / 3 * 2 + innerRadius) // 2/3 from inner

    val separatorPaint = Paint()



    private fun getOvalForArc(center: Point, radius: Float) =
            RectF(center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius)

    init {
        sectorPaint.color = ContextCompat.getColor(ApplicationContextHolder.context, R.color.modification_button_color)
        sectorPaint.style = Paint.Style.FILL

        separatorPaint.color = Color.BLACK
        separatorPaint.style = Paint.Style.STROKE
        separatorPaint.strokeWidth = resources.getString(R.dimen.stroke_separator).toFloat()
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