package com.dmitrykazanbaev.virus_game.service

import android.graphics.Typeface
import android.content.Context
import java.util.HashMap


object FontCache {

    private val fontCache = HashMap<String, Typeface>()

    fun getTypeface(fontName: String, context: Context): Typeface? {
        var typeface: Typeface? = fontCache[fontName]

        if (typeface == null) {
            try {
                val folder = fontName.substringBeforeLast(".")
                val myTypeface = Typeface.createFromAsset(context.assets, "fonts/$folder/$fontName")
                typeface = myTypeface
            } catch (e: Exception) {
                return null
            }

            fontCache.put(fontName, typeface)
        }

        return typeface
    }
}