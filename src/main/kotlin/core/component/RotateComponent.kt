package core.component

import core.BaseEntity
import core.dto.UpdateContext

class RotateComponent(private val velocity: Float) : BaseComponent() {

    init {
        setUpdateObserver { entity: BaseEntity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        val dy = velocity * context.elapsedTime.toFloat()
        entity.transform.rotate(dy)
    }
}