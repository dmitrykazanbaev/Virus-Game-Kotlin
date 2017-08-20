package com.dmitrykazanbaev.virus_game

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

        horizontal_scroll_view_background.setOnTouchListener { _, _ -> true }
        vertical_scroll_view_background.setOnTouchListener { _, _ -> true }

        characteristic_window.visibility = View.INVISIBLE

        radiogroup.setOnCheckedChangeListener { group, viewId ->
            updateButtonColor(group)
            updateModificationButtonController(viewId)
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
                    }
                    else button.buttonPaint.color = button.unselectedColor
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
        modification_controller.addView(modificationButtonController)
    }
}
