package physics.dto

import org.joml.Vector2f
import java.util.UUID
import kotlin.math.sqrt

data class CircleCollisionBox(
    val center: Vector2f,
    val radius: Float,
    val componentId: UUID) {

    fun isCollidingWith(other: CircleCollisionBox): Boolean {
        val sub = Vector2f(other.center).sub(Vector2f(center))
        val sub2 = sub.mul(sub)
        val distance = sqrt(sub2.x + sub2.y)
        val r2 = other.radius + radius
        return distance <= r2
    }

}