package com.dmitrykazanbaev.virus_game.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button


class ModificationButton : Button {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, def: Int) : super(context, attrs, def)

    val path = Path()
    val paint = Paint()
    val center by lazy { Point(width / 2, height / 2) }
    val innerRadius = 50
    val outerRadius by lazy { minOf(width, height) / 2 }
    val innerOval by lazy {
        RectF(center.x - innerRadius.toFloat(), center.y - innerRadius.toFloat(),
                center.x + innerRadius.toFloat(), center.y + innerRadius.toFloat())
    }
    val outerOval by lazy {
        RectF(center.x - outerRadius.toFloat(), center.y - outerRadius.toFloat(),
                center.x + outerRadius.toFloat(), center.y + outerRadius.toFloat())
    }
    var r = Region()

    init {
        paint.color = Color.DKGRAY
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        path.reset()
        path.arcTo(innerOval, 270f, 90f)
        path.arcTo(outerOval, 360f, -90f)
        path.close()

        canvas?.let {
            canvas.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rectF = RectF()
        path.computeBounds(rectF, true)
        r.setPath(path, Region(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt()))
        if (r.contains(event.x.toInt(), event.y.toInt()))
            return super.onTouchEvent(event)
        else
            return false
    }
}