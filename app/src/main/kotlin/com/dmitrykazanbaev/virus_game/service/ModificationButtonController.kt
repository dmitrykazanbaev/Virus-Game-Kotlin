package com.dmitrykazanbaev.virus_game.service

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.custom_views.ModificationButton

class ModificationButtonController
@JvmOverloads constructor(context: Context,
                          viewId: Int,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {

    val center by lazy { Point(height / 2, height / 2) }

    val sectorSeparatorPaint = Paint()

    var modificationButtons: List<ModificationButton> = when (viewId) {
        R.id.devices_button -> createDevicesButtonList()
        R.id.propagation_button -> createPropagationButtonList()
        R.id.abilities_button -> createAbilitiesButtonList()
        R.id.resistance_button -> createResistanceButtonList()
        else -> emptyList()
    }

    init {
        modificationButtons.forEach { addView(it) }

        sectorSeparatorPaint.color = Color.BLACK
        sectorSeparatorPaint.strokeWidth = resources.getString(R.dimen.stroke_separatorSector).toFloat()
        sectorSeparatorPaint.style = Paint.Style.STROKE
    }

    fun updateCenterWithX(x: Int) {
        center.x = x
        (0 until childCount)
                .map { getChildAt(it) as ModificationButton }
                .forEach {
                    it.center.x = x
                    it.invalidate()
                }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)

        modificationButtons.forEach {
            val sectorSeparatorLine =
                    getSectorSeparatorLine(it.innerRadius, it.outerRadius,
                            it.startAngle, it.sweepAngle)

            canvas?.drawLine(sectorSeparatorLine[0].x.toFloat(),
                    sectorSeparatorLine[0].y.toFloat(),
                    sectorSeparatorLine[1].x.toFloat(),
                    sectorSeparatorLine[1].y.toFloat(), sectorSeparatorPaint)
        }

    }

    private fun getSectorSeparatorLine(innerRadius: Float, outerRadius: Float,
                                       startAngle: Float, sweepAngle: Float): MutableList<Point> {
        val startX = center.x + innerRadius * Math.cos((startAngle + sweepAngle) * Math.PI / 180)
        val startY = center.y + innerRadius * Math.sin((startAngle + sweepAngle) * Math.PI / 180)
        val stopX = center.x + outerRadius * Math.cos((startAngle + sweepAngle) * Math.PI / 180)
        val stopY = center.y + outerRadius * Math.sin((startAngle + sweepAngle) * Math.PI / 180)

        return mutableListOf(Point(startX.toInt(), startY.toInt()), Point(stopX.toInt(), stopY.toInt()))
    }
}

private fun createPropagationButtonList(): List<ModificationButton> {
    val buttonList = mutableListOf<ModificationButton>()

    val countButtons = 4
    val sectorPartDegree = 360f / countButtons
    var startAngle = -45f - sectorPartDegree
    repeat(countButtons) {
        startAngle += sectorPartDegree
        buttonList.add(createModificationButton(startAngle, sectorPartDegree))
    }

    buttonList[0].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.wifi)

    buttonList[1].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.bluetooth)

    buttonList[2].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.ethernet)

    buttonList[3].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.stats_net)

    return buttonList
}

private fun createAbilitiesButtonList(): List<ModificationButton> {
    val buttonList = mutableListOf<ModificationButton>()

    val countButtons = 3
    val sectorPartDegree = 360f / countButtons
    var startAngle = -90f - sectorPartDegree
    repeat(countButtons) {
        startAngle += sectorPartDegree
        buttonList.add(createModificationButton(startAngle, sectorPartDegree))
    }

    buttonList[0].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.theif)

    buttonList[1].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.control)

    buttonList[2].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.spam)

    return buttonList
}

private fun createResistanceButtonList(): List<ModificationButton> {
    val buttonList = mutableListOf<ModificationButton>()

    val countButtons = 3
    val sectorPartDegree = 360f / countButtons
    var startAngle = -90f - sectorPartDegree
    repeat(countButtons) {
        startAngle += sectorPartDegree
        buttonList.add(createModificationButton(startAngle, sectorPartDegree))
    }

    buttonList[0].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.mask)

    buttonList[1].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.invisible)

    buttonList[2].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.new_virus)

    return buttonList
}

private fun createDevicesButtonList(): List<ModificationButton> {
    val buttonList = mutableListOf<ModificationButton>()

    val countButtons = 3
    val sectorPartDegree = 360f / countButtons
    var startAngle = -90f - sectorPartDegree
    repeat(countButtons) {
        startAngle += sectorPartDegree
        buttonList.add(createModificationButton(startAngle, sectorPartDegree))
    }

    buttonList[0].tag = "mobile"
    buttonList[0].setOnClickListener { onModificationButtonTouch(it) }
    buttonList[0].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.phone)

    buttonList[1].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.pc)

    buttonList[2].icon = ContextCompat.getDrawable(ApplicationContextHolder.context, R.mipmap.smart_home)

    return buttonList
}


private fun onModificationButtonTouch(view: View) {
    when (view.tag) {
        "mobile" -> {
            ((view as ModificationButton).parent as ModificationButtonController).
                    updateCenterWithX(view.width / 2)
            (view.parent as ModificationButtonController).invalidate()
        }
    }
}

private fun createModificationButton(startAngle: Float, sweepAngle: Float): ModificationButton {
    val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT)

    val button = ModificationButton(ApplicationContextHolder.context, startAngle, sweepAngle)

    button.setBackgroundColor(Color.TRANSPARENT)
    button.layoutParams = params

    return button
}