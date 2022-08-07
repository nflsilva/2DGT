package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Color
import render.dto.Particle

class BackgroundColorComponent(private val color: Color) : BaseComponent() {

    constructor(r: Float, g: Float, b: Float, a: Float) : this(Color(r, g, b, a))

    init {
        setUpdateObserver { _, context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.graphics.setBackgroundColor(color)
    }
}