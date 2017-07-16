package com.dmitrykazanbaev.virus_game

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import com.dmitrykazanbaev.virus_game.service.continueGame
import com.dmitrykazanbaev.virus_game.service.startNewGame


class StartMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_menu)

        ApplicationContextHolder.context = this
    }

    fun onTouch(view: View) {
        when (view.id) {
            R.id.start_game_button -> startNewGame()
            R.id.continue_game_button -> continueGame()
        }
    }
}