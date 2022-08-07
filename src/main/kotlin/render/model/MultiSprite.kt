package render.model

import render.dto.Sprite

class MultiSprite(val rows: Int,
                  val columns: Int) {

    private val sprites: Array<Array<Sprite?>> = Array(rows) { Array(columns) { null } }

    fun addSprite(row: Int, column: Int, sprite: Sprite){
        if(row < 0 || row >= rows || column < 0 || column >= columns) return
        sprites[row][column] = sprite
    }

    fun getSprite(row: Int, column: Int): Sprite? {
        if(row < 0 || row >= rows || column < 0 || column >= columns) return null
        return sprites[row][column]
    }
}