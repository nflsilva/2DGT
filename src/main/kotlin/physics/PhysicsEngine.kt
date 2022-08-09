package physics

import core.EngineConfiguration
import physics.dto.CircleCollisionBox
import java.util.UUID

class PhysicsEngine(private val configuration: EngineConfiguration) {

    private val collisionBoxes: MutableList<CircleCollisionBox> = mutableListOf()
    private val collisions: MutableMap<UUID, MutableList<UUID>> = mutableMapOf()

    fun onStart() {}
    fun onFrame() {}
    fun onUpdate() {

        collisions.clear()
        for(box0 in collisionBoxes){
            collisions[box0.componentId] = mutableListOf()
            for(box1 in collisionBoxes){
                if(box0 == box1) continue
                if(box0.isCollidingWith(box1)){
                    collisions[box0.componentId]?.add(box1.componentId)
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
    fun getCollisionsBox(componentUUID: UUID): List<UUID>? {
        return collisions[componentUUID]
    }

}