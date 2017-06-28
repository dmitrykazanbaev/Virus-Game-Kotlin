package com.dmitrykazanbaev.virus_game

import android.content.Context

class ApplicationContextSingleton {
    var applicationContext: Context? = null
    fun initialize(context: Context) {
        this.applicationContext = context
    }

    companion object {
        private var mInstance: ApplicationContextSingleton? = null
        val instance: ApplicationContextSingleton?
            get() {
                if (mInstance == null) mInstance = sync
                return mInstance
            }
        private val sync: ApplicationContextSingleton?
            @Synchronized get() {
                if (mInstance == null) mInstance = ApplicationContextSingleton()
                return mInstance
            }
    }
}