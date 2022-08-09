package core.component.render

import core.component.BaseComponent
import core.dto.UpdateContext
import render.dto.Color
import java.util.*

class BackgroundColorComponent(entityId: UUID, private val color: Color) : BaseComponent(entityId) {

    constructor(entityId: UUID, r: Float, g: Float, b: Float, a: Float) : this(entityId, Color(r, g, b, a))

    init {
        setUpdateObserver { _, context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.graphics.setBackgroundColor(color)
    }
}