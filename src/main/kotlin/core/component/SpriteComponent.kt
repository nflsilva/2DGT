package core.component

import core.BaseEntity
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Sprite
import render.dto.Transform

class SpriteComponent(private val spriteData: Sprite,
                      private val drawOffset: Vector2f = Vector2f().zero()
) : BaseComponent() {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        val transform = Transform(
            Vector2f(entity.transform.position).add(drawOffset),
            entity.transform.rotation,
            entity.transform.scale
        )
        context.graphics.render(spriteData, transform)
    }

}