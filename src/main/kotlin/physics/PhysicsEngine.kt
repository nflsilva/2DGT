package physics

import core.EngineConfiguration
import physics.dto.CircleCollisionBox
import physics.dto.CollisionContext
import java.util.*

class PhysicsEngine(private val configuration: EngineConfiguration) {

    private val collisionBoxes: MutableList<CircleCollisionBox> = mutableListOf()
    private val collisions: MutableMap<UUID, MutableList<CollisionContext>> = mutableMapOf()

    fun onStart() {}
    fun onFrame() {}
    fun onUpdate() {

        collisions.clear()
        for(box0 in collisionBoxes){
            collisions[box0.entityId] = mutableListOf()
            for(box1 in collisionBoxes){
                if(box0 == box1) continue
                val collisionResult = box0.isCollidingWith(box1)

                if(collisionResult.first){
                    collisions[box0.entityId]?.add(CollisionContext(box1.entityId, collisionResult.second))
                }
            }
        }
        collisionBoxes.clear()
    }
    fun onCleanUp() {

    }

    fun addCollisionBox(box: CircleCollisionBox){
        collisionBoxes.add(box)
    }
    fun getCollisionsBox(entityId: UUID): List<CollisionContext>? {
        return collisions[entityId]
    }

}