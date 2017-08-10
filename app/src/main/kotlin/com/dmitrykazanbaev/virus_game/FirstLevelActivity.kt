package com.dmitrykazanbaev.virus_game

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.custom_views.TrapezeButton
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import com.dmitrykazanbaev.virus_game.service.ModificationButtonController
import com.dmitrykazanbaev.virus_game.service.closeCharacteristicWindow
import com.dmitrykazanbaev.virus_game.service.showCharacteristicWindow
import io.realm.Realm
import kotlinx.android.synthetic.main.first_level_activity.*
import kotlinx.coroutines.experimental.runBlocking


class FirstLevelActivity : AppCompatActivity() {
    val firstLevelView by lazy { FirstLevelView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_level_activity)

        ApplicationContextHolder.context = this

        Realm.init(this)

        firstLevelView.holder.addCallback(firstLevelView)

        mainframe.addView(firstLevelView, 0)

        val modificationButtonController = ModificationButtonController(this)
        modificationButtonController.layoutParams =
                RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
        modification_and_menu.addView(modificationButtonController)

        horizontal_scroll_view_background.setOnTouchListener { _, _ -> true }
        vertical_scroll_view_background.setOnTouchListener { _, _ -> true }

        radiogroup.setOnCheckedChangeListener { _, _ ->
            for (i in 0 until radiogroup.childCount) {
                val button = radiogroup.getChildAt(i) as TrapezeButton
                if (button.isChecked) button.buttonPaint.color = Color.WHITE
                else button.buttonPaint.color = Color.BLUE
                button.invalidate()
            }
        }

        if (!intent.getBooleanExtra("new_game", false))
            firstLevelView.initLevelFromRealm()
    }

    override fun onStop() {
        firstLevelView.saveLevelToRealm()
        super.onStop()
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> {
                runBlocking {
                    firstLevelView.stopJobs()
                }
                showCharacteristicWindow()
            }
            R.id.close_characteristics_button -> {
                firstLevelView.startJobs()
                closeCharacteristicWindow()
            }
        R.id.trapezeButton, R.id.trapezeButton1 -> (view as TrapezeButton).isChecked = true
        }
    }
}
