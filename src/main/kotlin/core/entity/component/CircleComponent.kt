package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
import render.dto.Particle

class CircleComponent(private val particleData: Particle) : Component() {

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->
            context.graphics.render(particleData, entity.transform)
        }
    }
}