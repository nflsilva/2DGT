package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
import render.model.Color
import render.dto.Particle

class ParticleComponent(private val particle: Particle) : Component() {


    constructor(type: Int, size: Float, color: Color) : this(Particle(type, size, color))

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->
            context.graphics.render(particle, entity.transform)
        }
    }
}