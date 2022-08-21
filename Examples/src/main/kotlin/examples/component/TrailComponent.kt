package examples.component

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import java.util.*

class TrailComponent(entityId: UUID, private val length: Int) : BaseComponent(entityId) {

    private val lastPositionsQueue = ArrayDeque<Vector2f>()

    init {
        setUpdateObserver { entity: BaseEntity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        if (lastPositionsQueue.size == length) {
            lastPositionsQueue.poll()
        }
        lastPositionsQueue.offer(Vector2f(entity.transform.position))

    }

}