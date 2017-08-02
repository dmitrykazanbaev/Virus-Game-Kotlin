package com.dmitrykazanbaev.virus_game.model.virus

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.Button

class Virus(val propagation: PropagationComponent = PropagationComponent(),
            val abilities: AbilityComponent = AbilityComponent(),
            val resistance: ResistanceComponent = ResistanceComponent(),
            val devices: DeviceComponent = DeviceComponent())

class PropagationComponent(val wifi: Modification = Modification(),
                           val bluetooth: Modification = Modification(),
                           val ethernet: Modification = Modification(),
                           val mobile: Modification = Modification())

class AbilityComponent()

class ResistanceComponent(val antivirusDetecting: Modification = Modification(),
                          val masking: Modification = Modification(),
                          val partKernel: Modification = Modification())

class DeviceComponent(val smartphones: Modification = Modification(),
                      val computers: Modification = Modification(),
                      val smartHouses: Modification = Modification())

class Modification(val maxLevel: Int = 3,
                   var currentlevel: Int = 0,
                   var upgradeCost: Int = 0,
                   var imageName: String = "",
                   var title: String = "",
                   var description: String = "")

class ModificationButton : Button {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, def: Int) : super(context, attrs, def)

    val path = Path()
    val paint = Paint()
    val center by lazy { Point(width / 2, height / 2) }
    val innerOval by lazy { RectF(width / 2f - 50, height / 2f - 50, width / 2f + 50, height / 2f + 50) }
    val outerOval by lazy { RectF(width / 2f - 100, height / 2f - 100, width / 2f + 100, height / 2f + 100) }

    init {
        paint.color = Color.GREEN
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas?) {
        path.reset()
        path.arcTo(innerOval, 0f, 90f)
        path.arcTo(outerOval, 90f, -90f)
        path.close()

        canvas?.let {
            canvas.drawPath(path, paint)
        }
    }
}