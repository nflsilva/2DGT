package core.component.physics

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import java.util.*

class VerletIntegrationComponent(entityId: UUID,
                                 var mass: Float): BaseComponent(entityId) {

    private val acceleration = Vector2f().zero()
    private var previousPosition: Vector2f? = null

    init {
        setUpdateObserver { entity: BaseEntity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext)  {

        if(previousPosition == null){
            previousPosition = Vector2f(entity.transform.position)
        }

        val dt = context.elapsedTime.toFloat()
        val adt2 = Vector2f(acceleration).mul(dt*dt)

        val v = Vector2f(entity.transform.position).sub(previousPosition)
        previousPosition = Vector2f(entity.transform.position)

        val newPosition = Vector2f(entity.transform.position)
            .add(v)
            .add(adt2)

        entity.transform.position = newPosition
        acceleration.zero()
    }

    fun applyForce(force: Vector2f){
        acceleration.add(force.div(mass))
    }

}