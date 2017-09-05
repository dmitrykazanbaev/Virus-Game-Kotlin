package com.dmitrykazanbaev.virus_game.model.level

import android.animation.ValueAnimator
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Point
import android.graphics.RectF
import android.view.animation.LinearInterpolator
import java.util.*


class InfectedPhone(val center: Point = Point(),
                    val outerRadius: Float = 0f,
                    val minX: Int = 0, val minY: Int = 0,
                    val maxX: Int = 0, val maxY: Int = 0) {

    val innerRadius = outerRadius / 2

    val path = Path()

    var innerOval = calculateOvalForArc(center, innerRadius)
    var outerOval = calculateOvalForArc(center, outerRadius)

    val animationPath = Path()
    val animation = ValueAnimator.ofFloat(0.0f, 1.0f)

    init {
        refresh()

        createAnimationPath()

        animation.interpolator = LinearInterpolator()
        animation.repeatMode = ValueAnimator.REVERSE
        animation.repeatCount = ValueAnimator.INFINITE
        animation.duration = 20000L

        animation.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            internal var point = FloatArray(2)

            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = animation.animatedFraction
                val pathMeasure = PathMeasure(animationPath, true)
                pathMeasure.getPosTan(pathMeasure.length * value, point, null)
                center.x = point[0].toInt()
                center.y = point[1].toInt()

                refresh()
            }
        })
    }

    private fun createAnimationPath() {
        val random = Random()

        animationPath.reset()
        animationPath.moveTo(center.x.toFloat(), center.y.toFloat())
        val counts = 100
        var x1: Float = center.x.toFloat()
        var y1: Float = center.y.toFloat()
        var randX = if (random.nextBoolean()) -1 else 1
        var randY = if (random.nextBoolean()) -1 else 1
        for (i in 0 until counts) {
            val speed = 20
            var x2 = x1 + randX * speed * 0.5f
            val y2 = y1 + randY * speed * 0.5f
            if (i % 2 == 0) {
                x2 += 10
            } else {
                x2 -= 10
            }
            x1 += randX * speed
            y1 += randY * speed
            if (x1 > maxX || x1 < minX || i % 20 == 0) {
                randX *= -1
            }
            if (y1 > maxY || y1 < minY || i % 30 == 0) {
                randY *= -1
            }
            animationPath.quadTo(x2, y2, x1, y1)
        }
    }

    fun refresh() {
        innerOval = calculateOvalForArc(center, innerRadius)
        outerOval = calculateOvalForArc(center, outerRadius)

        synchronized(path) {
            path.reset()
            path.addArc(innerOval, 0f, 360f)
            path.addArc(outerOval, 360f, -360f)
            path.close()
        }
    }

    private fun calculateOvalForArc(center: Point, radius: Float) =
            RectF(center.x - radius, center.y - radius,
                    center.x + radius, center.y + radius)
}