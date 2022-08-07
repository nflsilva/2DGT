package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Color
import render.dto.Particle

class ParticleComponent(private val particle: Particle) : BaseComponent() {

    constructor(type: Int, size: Float, color: Color) : this(Particle(type, size, color))

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(particle, entity.transform)
    }
}