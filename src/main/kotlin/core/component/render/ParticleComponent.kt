package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import render.dto.Color
import render.dto.Particle
import java.util.*

class ParticleComponent(entityId: UUID, private val particle: Particle) : BaseComponent(entityId) {

    constructor(entityId: UUID, type: Int, size: Float, color: Color) : this(entityId, Particle(type, size, color))

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(particle, entity.transform)
    }
}