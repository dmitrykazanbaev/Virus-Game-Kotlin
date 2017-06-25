package com.dmitrykazanbaev.firstexample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }

    internal class DrawView(context : Context) : View(context) {
        var paint : Paint = Paint()

        init {
            paint.color = Color.GRAY
            paint.style = Paint.Style.FILL
        }

        override fun onDraw(canvas: Canvas?) {
            val path = Path()
            path.moveTo(100f, 100f)
            path.lineTo(100f, 50f)
            path.lineTo(200f, 50f)
            path.lineTo(150f, 150f)
            path.close()

            canvas?.drawPath(path, paint)
        }
    }
}
