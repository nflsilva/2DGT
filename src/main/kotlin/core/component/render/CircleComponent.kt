package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import render.dto.Particle
import java.util.*

class CircleComponent(entityId: UUID, private val particleData: Particle) : BaseComponent(entityId) {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(particleData, entity.transform)
    }
}