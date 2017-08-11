package com.dmitrykazanbaev.virus_game.custom_views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RadioButton
import com.dmitrykazanbaev.virus_game.R


class TrapezeButton
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : RadioButton(context, attrs, defStyleAttr) {

    val attributes: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TrapezeButton, 0, 0)

    val isIndentLeftTop = attributes.getBoolean(R.styleable.TrapezeButton_indentLeftTop, false)
    val isIndentLeftBottom = attributes.getBoolean(R.styleable.TrapezeButton_indentLeftBottom, false)
    val isIndentRightTop = attributes.getBoolean(R.styleable.TrapezeButton_indentRightTop, false)
    val isIndentRightBottom = attributes.getBoolean(R.styleable.TrapezeButton_indentRightBottom, false)

    val indentLeftRight by lazy { height / 2f }
    val buttonPath by lazy {
        val path = Path()

        if (isIndentLeftTop) path.moveTo(indentLeftRight, 0f)
        else path.moveTo(0f, 0f)

        if (isIndentRightTop) path.lineTo(width - indentLeftRight, 0f)
        else path.lineTo(width.toFloat(), 0f)

        if (isIndentRightBottom) path.lineTo(width - indentLeftRight, height.toFloat())
        else path.lineTo(width.toFloat(), height.toFloat())

        if (isIndentLeftBottom) path.lineTo(indentLeftRight, height.toFloat())
        else path.lineTo(0f, height.toFloat())

        path.close()

        path
    }
    val buttonPaint = Paint()
    val selectedColor = Color.WHITE
    val unselectedColor = attributes.getColor(R.styleable.TrapezeButton_backgroundColor, Color.BLUE)

    init {
        buttonPaint.color = unselectedColor
        buttonPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { canvas.drawPath(buttonPath, buttonPaint) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pathBounds = RectF()
        buttonPath.computeBounds(pathBounds, true)

        val buttonRegion = Region()
        buttonRegion.setPath(buttonPath, Region(pathBounds.left.toInt(), pathBounds.top.toInt(), pathBounds.right.toInt(), pathBounds.bottom.toInt()))

        if (buttonRegion.contains(event.x.toInt(), event.y.toInt()))
            return super.onTouchEvent(event)
        else
            return false
    }
}