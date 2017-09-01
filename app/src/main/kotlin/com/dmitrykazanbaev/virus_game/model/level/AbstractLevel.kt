package com.dmitrykazanbaev.virus_game.model.level

import com.dmitrykazanbaev.virus_game.model.dao.AbstractLevelDAO
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder


abstract class AbstractLevel(protected val jsonBuildingsResource: Int) {
    val applicationContext = ApplicationContextHolder.context

    abstract var width: Int
    abstract var height: Int

    abstract fun constructLevel()

    abstract fun getLevelState(): AbstractLevelDAO

    abstract fun setLevelState(levelState: AbstractLevelDAO)
}