package render.model

import org.joml.Vector2f
import render.dto.Sprite

class MultiSprite(val rows: Int,
                  val columns: Int) {

    data class SpriteInfo(val sprite: Sprite, val size: Vector2f)

    private val sprites: Array<Array<SpriteInfo?>> = Array(rows) { Array(columns) { null } }

    fun addSprite(row: Int, column: Int,  size: Vector2f, sprite: Sprite){
        if(row < 0 || row >= rows || column < 0 || column >= columns) return
        sprites[row][column] = SpriteInfo(sprite, size)
    }

    fun getSprite(row: Int, column: Int): SpriteInfo? {
        if(row < 0 || row >= rows || column < 0 || column >= columns) return null
        return sprites[row][column]
    }
}