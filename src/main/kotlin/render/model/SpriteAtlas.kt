package render.model

import org.joml.Vector2f
import render.dto.Sprite

open class SpriteAtlas(
    private val texture: Texture,
    private val numberOfRows: Int,
    private val numberOfColumns: Int
) {

    private val sprites: MutableMap<String, Sprite> = mutableMapOf()

    constructor(textureResource: String, numberOfRows: Int, numberOfColumns: Int) :
            this(Texture(textureResource), numberOfRows, numberOfColumns)

    fun setSprite(spriteSize: SpriteSizeEnum, name: String, row: Int, column: Int) {
        //TODO: Create exception for this
        if (column > numberOfColumns || row > numberOfRows || sprites.keys.contains(name)) {
            return
        }

        val spriteLeft = column * spriteSize.value / texture.width.toFloat()
        val spriteTop = row * spriteSize.value / texture.height.toFloat()
        val spriteBottom = spriteTop + spriteSize.value / texture.height.toFloat()
        val spriteRight = spriteLeft + spriteSize.value / texture.width.toFloat()

        sprites[name] = Sprite(
            spriteSize,
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