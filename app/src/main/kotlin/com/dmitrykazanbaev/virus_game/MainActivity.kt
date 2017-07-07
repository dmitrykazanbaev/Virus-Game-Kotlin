package com.dmitrykazanbaev.virus_game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import com.dmitrykazanbaev.virus_game.screen.FirstLevelView
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


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
}
