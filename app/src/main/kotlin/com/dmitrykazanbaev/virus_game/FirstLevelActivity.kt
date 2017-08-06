package com.dmitrykazanbaev.virus_game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.*
import io.realm.Realm
import kotlinx.android.synthetic.main.first_level_activity.*


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

        if (!intent.getBooleanExtra("new_game", false))
            firstLevelView.initLevelFromRealm()
    }

    override fun onStop() {
        firstLevelView.saveLevelToRealm()
        super.onStop()
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> showCharacteristicWindow()
            R.id.close_characteristics_button -> closeCharacteristicWindow()
        }
    }
}
