package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.beust.klaxon.JsonObject
import com.dmitrykazanbaev.virus_game.ApplicationContextSingleton
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel


abstract class AbstractLevelView(context : Context) : View(context) {
    val background : Bitmap? = BitmapFactory.decodeResource(resources, R.drawable.background)
    val paint = Paint()
    abstract val level : AbstractLevel

    init {
        ApplicationContextSingleton.instance?.initialize(context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawBitmap(background, 0f, 0f, paint)


    }
}