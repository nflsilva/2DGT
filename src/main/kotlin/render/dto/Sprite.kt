package render.dto

import org.joml.Vector2f
import render.model.SpriteSizeEnum
import render.model.Texture

data class Sprite(
    val size: SpriteSizeEnum,
    val texture: Texture,
    val startTextureCoordinates: Vector2f = Vector2f(0.0F),
    val endTextureCoordinates: Vector2f = Vector2f(1.0F)
) {

    constructor(
        spriteSize: SpriteSizeEnum,
        textureResource: String,
        startTextureCoordinates: Vector2f = Vector2f(0.0F),
        endTextureCoordinates: Vector2f = Vector2f(1.0F)
    ) :
            this(spriteSize, Texture(textureResource), startTextureCoordinates, endTextureCoordinates)
}