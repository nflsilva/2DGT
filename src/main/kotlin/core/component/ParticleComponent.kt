package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.model.Color
import render.dto.Particle

class ParticleComponent(private val particle: Particle) : Component() {

    constructor(type: Int, size: Float, color: Color) : this(Particle(type, size, color))

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(particle, entity.transform)
    }
}