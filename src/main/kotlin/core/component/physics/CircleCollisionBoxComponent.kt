package core.component.physics

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import physics.dto.CircleCollisionBox
import physics.dto.CollisionContext
import java.util.*

class CircleCollisionBoxComponent(entityId: UUID, private val centered: Boolean = true): BaseComponent(entityId) {

    var collisions: List<CollisionContext> = mutableListOf()
    init {
        setUpdateObserver { entity: BaseEntity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {

        context.physics.getCollisionsBox(entityId)?.let {
            collisions = it
        }

        val radius = entity.transform.scale.x / 2f
        val box = CircleCollisionBox(entity.transform.position, radius, entityId)
        context.physics.addCollisionBox(box)
    }

}