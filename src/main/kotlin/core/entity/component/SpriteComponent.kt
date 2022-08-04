package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
import render.dto.Sprite

class SpriteComponent(private val spriteData: Sprite) : Component() {

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->
            context.graphics.render(spriteData, entity.transform)
        }
    }
}