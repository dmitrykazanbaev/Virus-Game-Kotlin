package com.dmitrykazanbaev.virus_game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import com.dmitrykazanbaev.virus_game.service.closeCharacteristicWindow
import com.dmitrykazanbaev.virus_game.service.showCharacteristicWindow


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApplicationContextHolder.context = this

        val firstLevelView = FirstLevelView(this)
        firstLevelView.holder.addCallback(firstLevelView)

        val frameLayout = findViewById(R.id.mainframe) as FrameLayout

        frameLayout.addView(firstLevelView, 0)
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.virus_button -> showCharacteristicWindow()
            R.id.close_characteristics_button -> closeCharacteristicWindow()
        }
    }
}
