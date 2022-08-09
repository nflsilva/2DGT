package core.component.input

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import java.util.*

class RotateComponent(entityId: UUID, private val velocity: Float) : BaseComponent(entityId) {

    init {
        setUpdateObserver { entity: BaseEntity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        val dy = velocity * context.elapsedTime.toFloat()
        entity.transform.rotate(dy)
    }
}