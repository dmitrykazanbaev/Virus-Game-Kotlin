package com.dmitrykazanbaev.virus_game.custom_views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RadioButton
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.service.FontCache
import kotlin.properties.Delegates


class TrapezeButton
@JvmOverloads constructor(context: Context,
                          attrs: AttributeSet? = null,
                          defStyleAttr: Int = 0) : RadioButton(context, attrs, defStyleAttr) {


    var isIndentLeftTop by Delegates.notNull<Boolean>()
    var isIndentLeftBottom by Delegates.notNull<Boolean>()
    var isIndentRightTop by Delegates.notNull<Boolean>()
    var isIndentRightBottom by Delegates.notNull<Boolean>()

    private val indentLeftRight by lazy { height / 2f }

    private val buttonPath by lazy {
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
    var unselectedColor by Delegates.notNull<Int>()

    init {
        applyCustomFont(attrs)
        initFromAttributes(attrs)

        buttonPaint.color = unselectedColor
        buttonPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { canvas.drawPath(buttonPath, buttonPaint) }
        super.onDraw(canvas)
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

    private fun applyCustomFont(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.TrapezeButton)
            val fontName = attributes.getString(R.styleable.TrapezeButton_customFont)
            fontName?.let {
                val customTypeface = FontCache.getTypeface(fontName, context)
                customTypeface?.let { typeface = it }
            }
            attributes.recycle()
        }
    }

    private fun initFromAttributes(attrs: AttributeSet?) {
        val attributes = context.theme.obtainStyledAttributes(attrs, R.styleable.TrapezeButton, 0, 0)

        isIndentLeftTop = attributes.getBoolean(R.styleable.TrapezeButton_indentLeftTop, false)
        isIndentLeftBottom = attributes.getBoolean(R.styleable.TrapezeButton_indentLeftBottom, false)
        isIndentRightTop = attributes.getBoolean(R.styleable.TrapezeButton_indentRightTop, false)
        isIndentRightBottom = attributes.getBoolean(R.styleable.TrapezeButton_indentRightBottom, false)

        unselectedColor = attributes.getColor(R.styleable.TrapezeButton_backgroundColor, Color.BLUE)

        attributes.recycle()
    }
}
