package com.dmitrykazanbaev.virus_game

import android.graphics.Canvas
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RadioGroup
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.custom_views.TrapezeButton
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import com.dmitrykazanbaev.virus_game.service.ModificationButtonController
import com.dmitrykazanbaev.virus_game.service.closeCharacteristicWindow
import com.dmitrykazanbaev.virus_game.service.showCharacteristicWindow
import io.realm.Realm
import kotlinx.android.synthetic.main.first_level_activity.*
import kotlinx.coroutines.experimental.*
import java.text.SimpleDateFormat
import java.util.*


class FirstLevelActivity : AppCompatActivity() {
    private val firstLevelView by lazy { FirstLevelView(this) }
    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.US)
    private val calendar = GregorianCalendar()

    private var tickJob: Job? = null
    private var drawJob: Job? = null

    private fun startJobs() {
        initDrawJob()
        initTickJob()
    }

    private suspend fun stopJobs() {
        drawJob?.cancel()
        tickJob?.cancel()

        drawJob?.join()
        tickJob?.join()
    }

    private fun initDrawJob() {
        if (drawJob == null || drawJob?.isCompleted!!) {
            drawJob = launch(CommonPool) {
                var canvas: Canvas?
                while (isActive) {
                    canvas = firstLevelView.holder.lockCanvas()

                    synchronized(firstLevelView.holder) {
                        canvas?.let { firstLevelView.drawLevel(it) }
                    }

                    canvas?.let { firstLevelView.holder.unlockCanvasAndPost(it) }
                }
            }
        }
    }

    private fun initTickJob() {
        if (tickJob == null || tickJob?.isCompleted!!) {
            tickJob = launch(CommonPool) {
                while (isActive) {
                    firstLevelView.level.infect()
                    runOnUiThread { updateDate() }
                    delay(500)
                }
            }
        }
    }

    private fun updateDate() {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        date_button?.text = "  ${dateFormat.format(calendar.time)}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_level_activity)

        ApplicationContextHolder.context = this

        Realm.init(this)

        firstLevelView.holder.addCallback(firstLevelView)

        mainframe.addView(firstLevelView, 0)

        horizontal_scroll_view_background.setOnTouchListener { _, _ -> true }
        vertical_scroll_view_background.setOnTouchListener { _, _ -> true }

        characteristic_window.visibility = View.INVISIBLE

        radiogroup.setOnCheckedChangeListener { group, viewId ->
            updateButtonColor(group)
            updateModificationButtonController(viewId)
        }

        date_button.text = "  ${dateFormat.format(calendar.time)}"

        if (!intent.getBooleanExtra("new_game", false))
            firstLevelView.initLevelFromRealm()
    }

    override fun onResume() {
        startJobs()
        super.onResume()
    }

    override fun onPause() {
        runBlocking { stopJobs() }
        super.onPause()
    }

    override fun onStop() {
        firstLevelView.saveLevelToRealm()
        super.onStop()
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> {
                runBlocking {
                    stopJobs()
                }
                showCharacteristicWindow()
            }
            R.id.close_characteristics_button -> {
                startJobs()
                closeCharacteristicWindow()
            }
            R.id.devices_button, R.id.propagation_button,
            R.id.resistance_button, R.id.abilities_button -> if (!(view as TrapezeButton).isChecked) view.isChecked = true
        }
    }

    private fun updateButtonColor(group: RadioGroup) {
        (0 until group.childCount).
                map { i -> group.getChildAt(i) }.
                forEach {
                    val button = it as TrapezeButton
                    if (button.isChecked) {
                        button.buttonPaint.color = button.selectedColor
                    } else button.buttonPaint.color = button.unselectedColor
                    button.invalidate()
                }
    }

    private fun updateModificationButtonController(viewId: Int) {
        modification_controller.removeAllViews()

        val modificationButtonController = ModificationButtonController(this, sourceViewId = viewId)
        modificationButtonController.layoutParams =
                RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
        modificationButtonController.setOnCheckedChangeListener { radioGroup, _ -> radioGroup.invalidate() }
        modification_controller.addView(modificationButtonController)
    }
}
