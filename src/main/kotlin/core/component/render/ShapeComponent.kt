package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import java.util.*

class ShapeComponent(entityId: UUID,
                     private val shape: Shape,
                     private val centered: Boolean = true) : BaseComponent(entityId) {

    constructor(entityId: UUID, type: Shape.Type, color: Color, centered: Boolean = true) :
            this(entityId, Shape(type.value, color), centered)

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        var transform: Transform? = null
        if(!centered){
            transform = Transform(
                Vector2f(entity.transform.position)
                    .add(Vector2f(entity.transform.scale).div(2f)),
                entity.transform.rotation,
                Vector2f(entity.transform.scale)
            )
        }

        context.graphics.render(shape, transform ?: entity.transform)
    }
}