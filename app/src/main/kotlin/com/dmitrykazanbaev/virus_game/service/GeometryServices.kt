package com.dmitrykazanbaev.virus_game.service

import android.graphics.Path
import android.graphics.Point
import java.util.Comparator


fun getSortedPointsByClockwiseFromLeft(list: List<Point>): List<Point> {
    if (list.isNotEmpty()) {
        val leftPoint = list.minWith(compareBy(Point::x).thenBy(Point::y))

        return list.sortedWith(comparePointsByClockwiseFrom(leftPoint!!))
    }

    return list
}

fun getFigurePath(points: List<Point>): Path {
    val path: Path = Path()

    if (points.isNotEmpty()) {
        path.moveTo(points[0].x.toFloat(), points[0].y.toFloat())
        for (i in 1 until points.size) {
            path.lineTo(points[i].x.toFloat(), points[i].y.toFloat())
        }
        path.close()
    }

    return path
}

private class comparePointsByClockwiseFrom(val center: Point) : Comparator<Point> {
    override fun compare(p0: Point, p1: Point): Int {
        if (p0.x - center.x >= 0 && p1.x - center.x < 0) {
            return 1
        }
        if (p0.x - center.x < 0 && p1.x - center.x >= 0) {
            return -1
        }

        if (p0.x - center.x == 0 && p1.x - center.x == 0) {
            if (p0.y - center.y >= 0 || p1.y - center.y >= 0) {
                return p0.y.compareTo(p1.y)
            }
            return p1.y.compareTo(p0.y)
        }

        // compute the cross product of vectors (center -> a) x (center -> b)
        val det = (p0.x - center.x) * (p1.y - center.y) - (p1.x - center.x) * (p0.y - center.y)
        if (det < 0) {
            return 1
        }
        if (det > 0) {
            return -1
        }

        // points a and b are on the same line from the center
        // check which point is closer to the center
        val d1 = (p0.x - center.x) * (p0.x - center.x) + (p0.y - center.y) * (p0.y - center.y)
        val d2 = (p1.x - center.x) * (p1.x - center.x) + (p1.y - center.y) * (p1.y - center.y)
        return d1.compareTo(d2)
    }
}