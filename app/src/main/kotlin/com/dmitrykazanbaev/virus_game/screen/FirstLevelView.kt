package com.dmitrykazanbaev.virus_game.screen

import android.content.Context
import com.dmitrykazanbaev.virus_game.model.level.AbstractLevel
import com.dmitrykazanbaev.virus_game.model.level.FirstLevel

class FirstLevelView(context: Context) : AbstractLevelView(context) {
    override val level: AbstractLevel
        get() = FirstLevel()

    init {
        level
    }
}