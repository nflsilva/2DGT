package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Sprite

class SpriteComponent(private val spriteData: Sprite) : Component() {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(spriteData, entity.transform)
    }
}