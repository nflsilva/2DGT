package render.dto

import org.joml.Vector2f
import render.model.Texture

data class Sprite(
    val texture: Texture,
    val startTextureCoordinates: Vector2f = Vector2f(0.0F),
    val endTextureCoordinates: Vector2f = Vector2f(1.0F)){

    constructor(resourceName: String): this(Texture(resourceName))

}