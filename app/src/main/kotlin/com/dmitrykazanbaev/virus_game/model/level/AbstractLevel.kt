package com.dmitrykazanbaev.virus_game.model.level

import com.dmitrykazanbaev.virus_game.model.dao.AbstractLevelDAO
import com.dmitrykazanbaev.virus_game.service.ApplicationContextHolder
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch


abstract class AbstractLevel(protected val jsonBuildingsResource: Int) {
    val applicationContext = ApplicationContextHolder.context

    abstract var width: Int
    abstract var height: Int

    lateinit var tickJob: Job

    fun initTickJob() {
        tickJob = launch(CommonPool) {
            while (isActive) {
                infect()
                delay(500)
            }
        }
    }

    abstract fun constructLevel()

    abstract fun infect()

    abstract fun getLevelState(): AbstractLevelDAO

    abstract fun setLevelState(levelState: AbstractLevelDAO)
}