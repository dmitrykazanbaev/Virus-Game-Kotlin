package com.dmitrykazanbaev.virus_game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.beust.klaxon.*
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(DrawView(this))
    }

    internal class DrawView(context : Context) : View(context) {
        override fun onDraw(canvas: Canvas?) {
            canvas?.drawColor(Color.DKGRAY)

            val buildings = getBuildings(resources.openRawResource(R.raw.house_fin))
            buildings.forEach {
                drawBuilding(it as JsonObject, canvas)
            }
        }

        fun getBuildings(input : InputStream) : JsonArray<*> {
            val parser: Parser = Parser()
            return parser.parse(input) as JsonArray<*>
        }

        fun drawBuilding(building : JsonObject, canvas: Canvas?) {
            drawLeftSideBuilding(building, canvas)
            drawCenterSideBuilding(building, canvas)
            drawRoofBuilding(building, canvas)
        }

        fun drawLeftSideBuilding(building: JsonObject, canvas: Canvas?) {
            val paint = getFillingPaint()
            paint.color = R.color.colorLeft

            var path : Path = Path()
            building.string("left")?.let {
                path = drawFigureWithPath(it)
            }
            canvas?.drawPath(path, paint)
        }

        fun drawCenterSideBuilding(building: JsonObject, canvas: Canvas?) {
            val paint = getFillingPaint()
            paint.color = R.color.colorCenter

            var path : Path = Path()
            building.string("center")?.let {
                path = drawFigureWithPath(it)
            }
            canvas?.drawPath(path, paint)
        }

        fun drawRoofBuilding(building: JsonObject, canvas: Canvas?) {
            val paint = getFillingPaint()
            paint.color = Color.WHITE

            var path : Path = Path()
            building.string("roof")?.let {
                path = drawFigureWithPath(it)
            }
            canvas?.drawPath(path, paint)
        }

        fun drawFigureWithPath(building: String): Path {
            val path : Path = Path()
            val coordinates = building.split(",")

            path.moveTo(coordinates[0].toFloat(), coordinates[1].toFloat())
            for (i in 2 until coordinates.size step 2) {
                path.lineTo(coordinates[i].toFloat(), coordinates[i + 1].toFloat())
            }
            path.close()

            return path
        }

        fun getFillingPaint(): Paint {
            val paint = Paint()
            paint.style = Paint.Style.FILL
            return paint
        }
    }
}
