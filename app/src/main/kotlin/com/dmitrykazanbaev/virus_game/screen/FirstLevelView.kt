package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.model.dao.FirstLevelDAO
import com.dmitrykazanbaev.virus_game.model.level.Building
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import io.realm.Realm
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*


class FirstLevelView(context: Context) : AbstractLevelView(context, FirstLevel()) {
    private val realm = Realm.getDefaultInstance()!!

    private val paintForFilling = Paint()
    private val paintForStroke = Paint()

    private val colorLeft = ContextCompat.getColor(context, R.color.colorLeft)
    private val colorCenter = ContextCompat.getColor(context, R.color.colorCenter)
    private val colorRoof = ContextCompat.getColor(context, R.color.colorRoof)
    private val colorInfectedRoof = ContextCompat.getColor(context, R.color.control_button_color)
    private val colorBackground = ContextCompat.getColor(context, R.color.colorBackground)

    private val paint = Paint()

    val coinButtonView = CoinButtonView()

    init {
        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.isAntiAlias = true
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK

        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.control_button_color)
    }

    private fun translateXYToLocalCoordinates(x: Float, y: Float): Point {
        val mClickCoords = FloatArray(2)

        mClickCoords[0] = x
        mClickCoords[1] = y

        val matrix = Matrix()
        matrix.set(getMatrix())

        matrix.preScale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        matrix.preTranslate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        matrix.invert(matrix)

        matrix.mapPoints(mClickCoords)

        return Point(mClickCoords[0].toInt(), mClickCoords[1].toInt())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        coinButtonView.dispatchTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun drawLevel(canvas: Canvas) {
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)
        canvas.translate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        canvas.drawColor(colorBackground)

        (level as FirstLevel).infectedPhonesToDraw.forEach {
            synchronized(it.path) { canvas.drawPath(it.path, paint) }
        }

        level.buildings.forEach {
            drawBuilding(it, canvas)
        }

        coinButtonView.drawCoin(canvas)
    }

    override fun saveLevelToRealm() {
        realm.executeTransaction {
            it.where(FirstLevelDAO::class.java).findAll().deleteAllFromRealm()

            val firstLevelDAO = level.getLevelState()

            it.copyToRealm(firstLevelDAO as FirstLevelDAO)
        }
    }

    override fun initLevelFromRealm() {
        val firstLevelDAO = realm.where(FirstLevelDAO::class.java).findFirst()

        firstLevelDAO?.let { level.setLevelState(it) }
    }

    fun drawBuilding(building: Building, canvas: Canvas?) {
        drawLeftSideBuilding(building, canvas)
        drawCenterSideBuilding(building, canvas)
        drawRoofBuilding(building, canvas)
        drawInfectedRoofBuilding(building, canvas)

        canvas?.drawPath(building.roof, paintForStroke)
    }

    private fun drawLeftSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorLeft

        canvas?.drawPath(building.leftSide, paintForFilling)
        canvas?.drawPath(building.leftSide, paintForStroke)
    }

    private fun drawCenterSideBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorCenter

        canvas?.drawPath(building.centerSide, paintForFilling)
        canvas?.drawPath(building.centerSide, paintForStroke)
    }

    private fun drawRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorRoof

        canvas?.drawPath(building.roof, paintForFilling)
    }

    private fun drawInfectedRoofBuilding(building: Building, canvas: Canvas?) {
        paintForFilling.color = colorInfectedRoof

        canvas?.drawPath(building.infectedRoof, paintForFilling)
    }

    inner class CoinButtonView {
        val coin = ImageButton(context)
        var showTimeJob: Job? = null
        var money: Int = 0

        init {
            coin.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.plusmoney))
            coin.setBackgroundColor(Color.TRANSPARENT)
            coin.scaleType = ImageView.ScaleType.CENTER_INSIDE
            coin.layout(0, 0, 120, 120)
            coin.x = 0f
            coin.y = 0f
            coin.visibility = View.GONE
            coin.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    val touchPoint = translateXYToLocalCoordinates(motionEvent.x, motionEvent.y)
                    if (touchPoint.x < coin.x + coin.right && touchPoint.x > coin.x &&
                            touchPoint.y < coin.bottom + coin.y && touchPoint.y > coin.y)

                        coin.performClick()
                }
                true
            }
            coin.setOnTouchListener(object : View.OnTouchListener {
                var x: Float = 0f
                var y: Float = 0f

                override fun onTouch(p0: View?, motionEvent: MotionEvent): Boolean {
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            x = motionEvent.x
                            y = motionEvent.y
                        }
                        MotionEvent.ACTION_UP -> {
                            if (x == motionEvent.x && y == motionEvent.y &&
                                    coin.visibility == View.VISIBLE) {
                                val touchPoint = translateXYToLocalCoordinates(motionEvent.x, motionEvent.y)
                                if (touchPoint.x < coin.x + coin.right && touchPoint.x > coin.x &&
                                        touchPoint.y < coin.bottom + coin.y && touchPoint.y > coin.y)

                                    coin.performClick()
                            }
                        }
                    }
                    return true
                }

            })
            coin.setOnClickListener { increaseUserBalance() }
        }

        fun showCoin(increasingMoney: Int) {
            if (coin.visibility == View.GONE) {
                money = increasingMoney
                val random = Random()
                coin.x = random.nextInt(((level as FirstLevel).maxPoint.x * 0.8f - level.minPoint.x * 1.2f).toInt()) + level.minPoint.x * 1.2f
                coin.y = random.nextInt((level.maxPoint.y * 0.8f - level.minPoint.y * 1.2f).toInt()) + level.minPoint.y * 1.2f
                coin.visibility = View.VISIBLE
                showTimeJob = launch(CommonPool) {
                    delay(10000L)
                    if (isActive) coin.visibility = View.GONE
                }
            }
        }

        fun increaseUserBalance() {
            coin.visibility = View.GONE
            val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity
            firstLevelActivity.user.balance += money
            showTimeJob?.cancel()
        }

        fun dispatchTouchEvent(event: MotionEvent?) {
            coin.dispatchTouchEvent(event)
        }

        fun drawCoin(canvas: Canvas) {
            if (coin.visibility == View.VISIBLE) {
                canvas.save()
                canvas.translate(coin.x, coin.y)
                coin.draw(canvas)
                canvas.restore()
            }
        }
    }
}