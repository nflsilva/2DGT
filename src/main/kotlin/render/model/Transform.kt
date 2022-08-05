package render.model

import org.joml.Vector2f

class Transform(
    val position: Vector2f,
    var rotation: Float,
    val scale: Vector2f) {

    fun translate(x: Float, y: Float){
        position.add(Vector2f(x, y))
    }
    fun rotate(angle: Float){
        rotation += angle
    }

}