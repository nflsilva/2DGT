package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext

class RotateComponent(private val velocity: Float) : Component() {

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->
            val dy = velocity * context.elapsedTime.toFloat()
            entity.transform.rotate(dy)
        }
    }
}