package render.model

import org.joml.Vector2f
import render.dto.Sprite

open class SpriteAtlas(
    private val texture: Texture,
    private val numberOfRows: Int,
    private val numberOfColumns: Int
) {

    private val spriteSize: Vector2f = Vector2f(
        texture.width / numberOfColumns.toFloat(),
        texture.height / numberOfRows.toFloat())

    private val sprites: MutableMap<String, Sprite> = mutableMapOf()

    constructor(textureResource: String, numberOfRows: Int, numberOfColumns: Int) :
            this(Texture(textureResource), numberOfRows, numberOfColumns)

    fun setSprite(name: String, row: Int, column: Int, multiSprite: Vector2f = Vector2f(1f)) {

        //TODO: Create exception for this
        if (column > numberOfColumns || row > numberOfRows || sprites.keys.contains(name)) {
            return
        }

        val spriteLeft = column * spriteSize.x / texture.width.toFloat()
        val spriteTop = row * spriteSize.y / texture.height.toFloat()
        val spriteBottom = spriteTop + (spriteSize.y * multiSprite.y) / texture.height.toFloat()
        val spriteRight = spriteLeft + (spriteSize.x  * multiSprite.x)/ texture.width.toFloat()

        sprites[name] = Sprite(
            texture,
            Vector2f(spriteLeft, spriteTop),
            Vector2f(spriteRight, spriteBottom)
        )
    }

    fun getSprite(name: String): Sprite {
        if (name in sprites) {
            return sprites[name]!!
        }
        //TODO: Improve this
        throw java.lang.Exception("The sprite '${name}' does not exist.")
    }

}