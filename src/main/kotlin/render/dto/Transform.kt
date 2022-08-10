package render.dto

import org.joml.Vector2f

data class Transform(
    var position: Vector2f,
    var rotation: Float,
    var scale: Vector2f) {

    fun translate(value: Vector2f){
        position.add(value)
    }
    fun translate(x: Float, y: Float){
        position.add(Vector2f(x, y))
    }
    fun rotate(angle: Float){
        rotation += angle
    }
}