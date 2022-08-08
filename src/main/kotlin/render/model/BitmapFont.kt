package render.model

import org.joml.Vector2f
import render.dto.Sprite

open class BitmapFont(
    private val texture: Texture,
    private val numberOfRows: Int,
    private val numberOfColumns: Int,
    private var charSize: Vector2f? = null,
    ) {

    constructor(textureResource: String, numberOfRows: Int, numberOfColumns: Int, charSize: Vector2f? = null) :
            this(Texture(textureResource), numberOfRows, numberOfColumns, charSize)

    init {
        if(charSize == null){
            charSize = Vector2f(
                texture.width / numberOfColumns.toFloat(),
                texture.height / numberOfRows.toFloat())
        }
    }

    private val characters: MutableMap<Char, Sprite> = mutableMapOf()

    fun setRow(row: Int, chars: String){
        for( (i, c) in chars.withIndex()){
            setCharacter(c, row, i)
        }
    }

    fun setCharacter(char: Char, row: Int, column: Int) {

        if (column > numberOfColumns || row > numberOfRows) return

        val charLeft = column * charSize!!.x / texture.width
        val charTop = row * charSize!!.y / texture.height
        val charBottom = charTop + charSize!!.y / texture.height
        val charRight = charLeft + charSize!!.x / texture.width

        characters[char] = Sprite(
            texture,
            Vector2f(charLeft, charTop),
            Vector2f(charRight, charBottom)
        )
    }

    fun getCharacter(char: Char): Sprite {
        characters[char]?.let {
            return it
        }
        throw Exception("Character '$char' was not found.")
    }

}