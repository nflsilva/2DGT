package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Particle

class CircleComponent(private val particleData: Particle) : Component() {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(particleData, entity.transform)
    }
}