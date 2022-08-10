package physics.dto

import org.joml.Math.sqrt
import org.joml.Vector2f
import java.util.*

data class CircleCollisionBox(
    val center: Vector2f,
    val radius: Float,
    val entityId: UUID) {

    fun isCollidingWith(other: CircleCollisionBox): Pair<Boolean, Float> {
        val sub = Vector2f(other.center).sub(Vector2f(center))
        val sub2 = sub.mul(sub)
        val distance = sqrt(sub2.x + sub2.y)
        val r2 = other.radius + radius
        return Pair(distance <= r2, distance)
    }

}