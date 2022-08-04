package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
import render.model.Color
import render.dto.Shape

class ShapeComponent(private val shape: Shape) : Component() {

    constructor(type: Shape.Type, color: Color) : this(Shape(type.value, color))

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->
            context.graphics.render(shape, entity.transform)
        }
    }
}