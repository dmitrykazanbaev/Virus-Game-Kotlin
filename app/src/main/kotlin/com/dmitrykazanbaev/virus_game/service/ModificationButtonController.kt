package com.dmitrykazanbaev.virus_game.service

import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import com.dmitrykazanbaev.virus_game.custom_views.ModificationButton

fun createDevicesButtonList(): List<View> {
    val buttonList = mutableListOf<ModificationButton>()

    val countButtons = 3
    val sectorPartDegree = 360f / countButtons
    repeat (countButtons) {
        val startAngle = it * sectorPartDegree
        buttonList.add(createModificationButton(startAngle, sectorPartDegree))
    }

    return buttonList
}

fun createModificationButton(startAngle: Float, sweepAngle: Float) : ModificationButton {
    val button = ModificationButton(ApplicationContextHolder.context, startAngle, sweepAngle)
    val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT)
    button.setBackgroundColor(Color.TRANSPARENT)
    button.layoutParams = params

    return button
}