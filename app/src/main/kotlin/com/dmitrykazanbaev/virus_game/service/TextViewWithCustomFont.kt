package com.dmitrykazanbaev.virus_game.service

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.dmitrykazanbaev.virus_game.R


class TextViewWithCustomFont : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyCustomFont(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        applyCustomFont(attrs)
    }

    private fun applyCustomFont(attrs: AttributeSet?) {
        attrs?.let {
            val attributes = context.obtainStyledAttributes(it, R.styleable.TextViewWithCustomFont)
            val fontName = attributes.getString(R.styleable.TextViewWithCustomFont_customFont)
            fontName?.let {
                val customTypeface = FontCache.getTypeface(fontName, context)
                customTypeface?.let { typeface = it }
            }
            attributes.recycle()
        }
    }
}