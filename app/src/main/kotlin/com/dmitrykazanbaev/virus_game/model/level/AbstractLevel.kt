package com.dmitrykazanbaev.virus_game.model.level

import android.content.Context
import com.dmitrykazanbaev.virus_game.model.dao.AbstractLevelDAO


abstract class AbstractLevel(val applicationContext: Context, protected val jsonBuildingsResource: Int) {

    abstract var width: Int
    abstract var height: Int

    abstract var centerX: Int
    abstract var centerY: Int

    abstract fun constructLevel()

    abstract fun getLevelState(): AbstractLevelDAO

    abstract fun setLevelState(levelState: AbstractLevelDAO)
}