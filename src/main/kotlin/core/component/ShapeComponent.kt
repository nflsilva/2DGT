package core.component

import core.BaseEntity
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform

class ShapeComponent(private val shape: Shape,
                     private val centered: Boolean = true) : BaseComponent() {

    constructor(type: Shape.Type, color: Color, centered: Boolean = true) :
            this(Shape(type.value, color), centered)

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