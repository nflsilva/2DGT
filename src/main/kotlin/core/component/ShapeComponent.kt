package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Color
import render.dto.Shape

class ShapeComponent(private val shape: Shape) : BaseComponent() {

    constructor(type: Shape.Type, color: Color) : this(Shape(type.value, color))

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(shape, entity.transform)
    }
}