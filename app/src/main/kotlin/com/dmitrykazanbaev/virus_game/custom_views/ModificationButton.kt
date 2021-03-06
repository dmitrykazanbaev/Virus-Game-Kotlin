package com.dmitrykazanbaev.virus_game.custom_views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RadioButton
import com.dmitrykazanbaev.virus_game.R


class ModificationButton
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0,
                          var modificationLevel: Int = 0,
                          var startAngle: Float = 0f,
                          val sweepAngle: Float = 0f) : RadioButton(context, attrs, defStyleAttr) {

    lateinit var icon: Drawable

    private val sectorPath = Path()
    private val sectorPaint = Paint()

    val center by lazy { Point(height / 2, height / 2) }
    var iconCenter = Point()
        get() = calculateIconCenter()

    private val iconSize by lazy {
        val widthCoef = icon.intrinsicWidth / ((outerRadius - innerRadius) / 2)
        val heightCoef = icon.intrinsicHeight / ((outerRadius - innerRadius) / 2)
        val maxCoef = maxOf(widthCoef, heightCoef)

        Pair(icon.intrinsicWidth / maxCoef, icon.intrinsicHeight / maxCoef)
    }

    val outerRadius by lazy { height / 2f }
    val innerRadius by lazy { outerRadius / 2 }

    var innerOval = RectF()
        get() = calculateOvalForArc(center, innerRadius)
    var outerOval = RectF()
        get() = calculateOvalForArc(center, outerRadius)

    var firstSeparatorOval = RectF()
        get() = calculateOvalForArc(center, (outerRadius - innerRadius) / 3 + innerRadius) // 1/3 from inner

    var secondSeparatorOval = RectF()
        get() = calculateOvalForArc(center, (outerRadius - innerRadius) / 3 * 2 + innerRadius) // 2/3 from inner

    private val separatorPaint = Paint()

    //private val checkedSectorPaint = Paint()

    private val firstLevelModificationSectorPaint = Paint()
    private val firstLevelModificationSector by lazy {
        val path = Path()
        path.arcTo(innerOval, startAngle, sweepAngle)
        path.arcTo(firstSeparatorOval, startAngle + sweepAngle, -sweepAngle)
        path.close()

        path
    }

    private val secondLevelModificationSectorPaint = Paint()
    private val secondLevelModificationSector by lazy {
        val path = Path()
        path.arcTo(firstSeparatorOval, startAngle, sweepAngle)
        path.arcTo(secondSeparatorOval, startAngle + sweepAngle, -sweepAngle)
        path.close()

        path
    }

    private val thirdLevelModificationSectorPaint = Paint()
    private val thirdLevelModificationSector by lazy {
        val path = Path()
        path.arcTo(secondSeparatorOval, startAngle, sweepAngle)
        path.arcTo(outerOval, startAngle + sweepAngle, -sweepAngle)
        path.close()

        path
    }


    private fun calculateOvalForArc(center: Point, radius: Float) =
            RectF(center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius)

    private fun calculateIconCenter(): Point {
        val x = center.x + (outerRadius + innerRadius) / 2 * Math.cos((startAngle + sweepAngle / 2) * Math.PI / 180)
        val y = center.y + (outerRadius + innerRadius) / 2 * Math.sin((startAngle + sweepAngle / 2) * Math.PI / 180)
        return Point(x.toInt(), y.toInt())
    }

    init {
        sectorPaint.color = ContextCompat.getColor(context, R.color.modification_button_color)
        sectorPaint.isAntiAlias = true

        firstLevelModificationSectorPaint.color = ContextCompat.getColor(context, R.color.first_level_modification)
        firstLevelModificationSectorPaint.isAntiAlias = true

        secondLevelModificationSectorPaint.color = ContextCompat.getColor(context, R.color.second_level_modification)
        secondLevelModificationSectorPaint.isAntiAlias = true

        thirdLevelModificationSectorPaint.color = ContextCompat.getColor(context, R.color.third_level_modification)
        thirdLevelModificationSectorPaint.isAntiAlias = true

        /*checkedSectorPaint.color = ContextCompat.getColor(ApplicationContextHolder.context, R.color.colorOrange)
        checkedSectorPaint.style = Paint.Style.STROKE*/

        separatorPaint.color = Color.BLACK
        separatorPaint.isAntiAlias = true
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

            drawSectorAccordingToModificationLevel(canvas, modificationLevel)

            canvas.drawArc(firstSeparatorOval, startAngle, sweepAngle, false, separatorPaint)
            canvas.drawArc(secondSeparatorOval, startAngle, sweepAngle, false, separatorPaint)

            icon.setBounds(iconCenter.x - iconSize.first.toInt() / 2,
                    iconCenter.y - iconSize.second.toInt() / 2,
                    iconCenter.x + iconSize.first.toInt() / 2,
                    iconCenter.y + iconSize.second.toInt() / 2)
            icon.draw(canvas)

            //if (isChecked) canvas.drawPath(sectorPath, checkedSectorPaint)
        }
    }

    private fun drawSectorAccordingToModificationLevel(canvas: Canvas, modificationLevel: Int) {
        if (modificationLevel >= 1) {
            canvas.drawPath(firstLevelModificationSector, firstLevelModificationSectorPaint)

            if (modificationLevel >= 2) canvas.drawPath(secondLevelModificationSector, secondLevelModificationSectorPaint)

            if (modificationLevel == 3) canvas.drawPath(thirdLevelModificationSector, thirdLevelModificationSectorPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pathBounds = RectF()
        sectorPath.computeBounds(pathBounds, true)

        val buttonRegion = Region()
        buttonRegion.setPath(sectorPath, Region(pathBounds.left.toInt(), pathBounds.top.toInt(), pathBounds.right.toInt(), pathBounds.bottom.toInt()))

        return if (buttonRegion.contains(event.x.toInt(), event.y.toInt()))
            super.onTouchEvent(event)
        else false
    }
}