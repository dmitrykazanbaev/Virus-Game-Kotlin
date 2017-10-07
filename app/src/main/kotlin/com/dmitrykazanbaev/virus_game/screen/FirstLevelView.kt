package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.dmitrykazanbaev.virus_game.FirstLevelActivity
import com.dmitrykazanbaev.virus_game.R
import com.dmitrykazanbaev.virus_game.custom_views.TextViewWithCustomFont
import com.dmitrykazanbaev.virus_game.model.dao.FirstLevelDAO
import com.dmitrykazanbaev.virus_game.model.level.Building
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import com.dmitrykazanbaev.virus_game.service.FontCache
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*


class FirstLevelView(context: Context) : AbstractLevelView(context) {
    override val level by lazy { FirstLevel(context) }
    private val paintForFilling = Paint()
    private val paintForStroke = Paint()

    private val colorLeft = ContextCompat.getColor(context, R.color.colorLeft)
    private val colorCenter = ContextCompat.getColor(context, R.color.colorCenter)
    private val colorRoof = ContextCompat.getColor(context, R.color.colorRoof)
    private val colorInfectedRoof = ContextCompat.getColor(context, R.color.control_button_color)
    private val colorBackground = ContextCompat.getColor(context, R.color.colorBackground)

    private val paint = Paint()

    val coinButtonView = CoinButtonView()
    val messageList by lazy {
        val list = mutableListOf<BubbleMessage>()
        repeat(5) {
            list.add(BubbleMessage(context))
        }
        list
    }

    init {
        paintForFilling.style = Paint.Style.FILL

        paintForStroke.style = Paint.Style.STROKE
        paintForStroke.isAntiAlias = true
        paintForStroke.strokeWidth = resources.getString(R.string.strokeWidth).toFloat()
        paintForStroke.color = Color.BLACK

        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.control_button_color)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        coinButtonView.dispatchTouchEvent(event)
        return super.onTouchEvent(event)
    }

    override fun drawLevel(canvas: Canvas) {
        canvas.scale(scaleFactor, scaleFactor, level.centerX.toFloat(), level.centerY.toFloat())
        canvas.translate(-xOffset / scaleFactor, -yOffset / scaleFactor)

        canvas.drawColor(colorBackground)

        level.infectedPhonesToDraw.forEach {
            synchronized(it.path) { canvas.drawPath(it.path, paint) }
        }

        level.buildings.forEach {
            drawBuilding(it, canvas)
        }

        messageList.forEach { it.draw(canvas) }

        coinButtonView.drawCoin(canvas)
    }

    override fun saveLevelToRealm() {
        realm.executeTransaction {
            it.where(FirstLevelDAO::class.java).findAll().deleteAllFromRealm()

            val firstLevelDAO = level.getLevelState()

            it.copyToRealm(firstLevelDAO)
        }
    }

    override fun initLevelFromRealm() {
        val firstLevelDAO = realm.where(FirstLevelDAO::class.java).findFirst()

        firstLevelDAO?.let { level.setLevelState(it) }
    }

    private fun drawBuilding(building: Building, canvas: Canvas?) {
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
        private var showTimeJob: Job? = null
        private var money: Int = 0

        init {
            coin.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.plusmoney))
            coin.setBackgroundColor(Color.TRANSPARENT)
            coin.scaleType = ImageView.ScaleType.CENTER_INSIDE
            coin.layout(0, 0, 120, 120)
            coin.x = 0f
            coin.y = 0f
            coin.visibility = View.GONE
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
            coin.setOnClickListener {
                increaseUserBalance()
                showTimeJob?.cancel()
            }
        }

        fun showCoin(increasingMoney: Int) {
            if (coin.visibility == View.GONE) {
                money = increasingMoney
                val random = Random()
                coin.x = random.nextInt((level.maxPoint.x * 0.8f - level.minPoint.x * 1.2f).toInt()) + level.minPoint.x * 1.2f
                coin.y = random.nextInt((level.maxPoint.y * 0.8f - level.minPoint.y * 1.2f).toInt()) + level.minPoint.y * 1.2f
                coin.visibility = View.VISIBLE
                showTimeJob = launch(CommonPool) {
                    delay(10000L)
                    if (isActive) coin.visibility = View.GONE
                }
            }
        }

        private fun increaseUserBalance() {
            coin.visibility = View.GONE
            val firstLevelActivity = ApplicationContextHolder.context as FirstLevelActivity
            firstLevelActivity.user.balance += money
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

    inner class BubbleMessage(context: Context) {
        private val textLayout = LinearLayout(context)
        private val textView = TextViewWithCustomFont(context)
        var isShown = false
        var x = 0
        var y = 0
        private val backgroundBubble = BubbleDrawable(context)

        init {
            textView.typeface = FontCache.getTypeface("DINPro/DINPro.otf", context)
            textView.textSize = height / 45f
            textView.maxWidth = width / 3
            textView.gravity = Gravity.CENTER
            textView.setTextColor(ContextCompat.getColor(context, R.color.message_color))
            textView.background = backgroundBubble

            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            textLayout.layoutParams = layoutParams
            textLayout.addView(textView)
        }

        fun showAtRandom(text: String) {
            updateXYByCenterRandomBuilding(level.buildings)
            show(text)
        }

        fun showAtInfected(text: String) {
            updateXYByCenterInfectedBuilding()
            show(text)
        }

        private fun show(text: String) {
            if (!isShown) {
                textView.text = text.toUpperCase()
                textView.measure(0, 0)
                updateXYToRealCenter()
                isShown = true
                launch(CommonPool) {
                    delay(4000L)
                    isShown = false
                }
            }
        }

        private fun updateXYByCenterInfectedBuilding(): Boolean {
            val infected = level.buildings.filter { it.infectedComputers > 0 || it.infectedSmartHome > 0 }
            if (infected.isNotEmpty()) {
                updateXYByCenterRandomBuilding(infected)

                return true
            }
            return false
        }

        private fun updateXYByCenterRandomBuilding(buildings: List<Building>) {
            val randomBuilding = buildings[Random().nextInt(buildings.size)]
            val center = randomBuilding.centerForMessage
            x = center.x
            y = center.y
        }

        private fun updateXYToRealCenter() {
            x += backgroundBubble.mPointerWidth
            y -= textView.measuredHeight - backgroundBubble.mPointerHeight / 2 - backgroundBubble.mCornerRad.toInt()
        }

        fun draw(canvas: Canvas) {
            if (isShown) {
                textLayout.measure(canvas.width, canvas.height)
                textLayout.layout(0, 0, canvas.width, canvas.height)

                canvas.save()
                canvas.translate(x.toFloat(), y.toFloat())

                textLayout.draw(canvas)

                canvas.restore()
            }
        }
    }

    class BubbleDrawable(val context: Context) : Drawable() {

        private val mPaint: Paint = Paint()

        private var mBoxRect = RectF()
        private var mBoxWidth: Int = 0
        private var mBoxHeight: Int = 0
        var mCornerRad: Float = 0f
        private val mBoxPadding = Rect()

        private val mPointer: Path = Path()
        var mPointerWidth: Int = 0
        var mPointerHeight: Int = 0

        init {
            initBubble()
            setPadding(40, 0, 40, 0)
        }

        // Setters
        ////////////////////////////////////////////////////////////

        private fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
            mBoxPadding.left = left
            mBoxPadding.top = top
            mBoxPadding.right = right
            mBoxPadding.bottom = bottom
        }

        private fun initBubble() {
            mPaint.isAntiAlias = true
            mPaint.color = Color.WHITE
            mCornerRad = 0f
            mPointerWidth = 20
            mPointerHeight = 20
        }

        private fun updatePointerPath() {
            mPointer.reset()

            // Set the starting point
            mPointer.moveTo(mPointerWidth.toFloat() + 1, mBoxHeight.toFloat() - mCornerRad) // plus 1 for hiding border

            // Define the lines
            mPointer.rLineTo(0f, -mPointerHeight.toFloat())
            mPointer.rLineTo(-mPointerWidth.toFloat(), mPointerHeight / 2f)
            mPointer.rLineTo(mPointerWidth.toFloat(), mPointerHeight / 2f)
            mPointer.close()
        }

        // Superclass Override Methods
        ////////////////////////////////////////////////////////////

        override fun draw(canvas: Canvas) {
            mBoxRect.left = mPointerWidth.toFloat()
            mBoxRect.top = 0f
            mBoxRect.right = mBoxWidth.toFloat()
            mBoxRect.bottom = mBoxHeight.toFloat()
            canvas.drawRoundRect(mBoxRect, mCornerRad, mCornerRad, mPaint)
            updatePointerPath()
            canvas.drawPath(mPointer, mPaint)
        }

        override fun getOpacity(): Int {
            return 255
        }

        override fun setAlpha(alpha: Int) {}

        override fun setColorFilter(cf: ColorFilter) {}

        override fun getPadding(padding: Rect): Boolean {
            padding.set(mBoxPadding)
            return true
        }

        override fun onBoundsChange(bounds: Rect) {
            mBoxWidth = bounds.width() - mPointerWidth
            mBoxHeight = getBounds().height()
            mCornerRad = mBoxHeight / 5f
            super.onBoundsChange(bounds)
        }
    }
}