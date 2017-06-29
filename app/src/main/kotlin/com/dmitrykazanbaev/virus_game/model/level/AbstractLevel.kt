package com.dmitrykazanbaev.virus_game.model.level

import com.beust.klaxon.JsonArray
import com.beust.klaxon.Parser
import com.dmitrykazanbaev.virus_game.service.ApplicationContextSingleton
import java.io.InputStream


abstract class AbstractLevel {
    val applicationContext = ApplicationContextSingleton.instance?.applicationContext

    lateinit var buildings : JsonArray<*>

    abstract protected fun initializeLevelWithBuildings()

    protected fun getBuildings(input : InputStream) : JsonArray<*> {
        val parser: Parser = Parser()
        return parser.parse(input) as JsonArray<*>
    }
}