package render.batch

import org.joml.Vector2f
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBindTexture
import render.dto.Sprite
import render.dto.Transform
import render.model.MultiSprite
import render.model.Texture

class SpriteBatch(
    private val maxSprites: Int,
    private val maxTextures: Int
) :
    BaseBatch(maxSprites, 4, 6) {

    private data class Quad(val tl: Vector2f, val bl: Vector2f, val br: Vector2f, val tr: Vector2f)

    private val textures: MutableList<Texture> = mutableListOf()

    companion object {
        const val POSITION_INDEX = 0
        const val TRANSLATION_INDEX = 1
        const val ROTATION_INDEX = 2
        const val SCALE_INDEX = 3
        const val TEXTURE_COORDS_INDEX = 4
        const val TEXTURE_INDEX = 5
    }

    init {
        addFloatAttributeBuffer(POSITION_INDEX, 2)
        addFloatAttributeBuffer(TRANSLATION_INDEX, 2)
        addFloatAttributeBuffer(ROTATION_INDEX, 1)
        addFloatAttributeBuffer(SCALE_INDEX, 2)
        addFloatAttributeBuffer(TEXTURE_COORDS_INDEX, 2)
        addIntAttributeBuffer(TEXTURE_INDEX, 1)
        addFloatAttributeBuffer(TEXTURE_INDEX, 2)
    }

    override fun bind() {
        super.bind()
        for (i in 0 until textures.size) {
            textures[i].bind(i)
        }
    }

    override fun unbind() {
        super.unbind()
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    private fun getQuad(translatedBy: Vector2f = Vector2f().zero()): Quad {
        return Quad(
            Vector2f(0f + translatedBy.x, 1f + translatedBy.y),
            Vector2f(0f + translatedBy.x, 0f + translatedBy.y),
            Vector2f(1f + translatedBy.x, 0f + translatedBy.y),
            Vector2f(1f + translatedBy.x, 1f + translatedBy.y)
        )
    }

    fun addSprite(sprite: Sprite, transform: Transform){
        addSprite(sprite, transform, Vector2f().zero())
    }

    fun addMultiSprite(multiSprite: MultiSprite, transform: Transform){

        val spriteSize = Vector2f(transform.scale)
            .div(Vector2f(multiSprite.columns.toFloat(), multiSprite.rows.toFloat()))

        var currentRowY = multiSprite.rows / -2f
        for(r in 0 until multiSprite.rows){
            var currentRowX = multiSprite.columns / -2f
            for(c in 0 until multiSprite.columns){
                val sprite = multiSprite.getSprite(r, c) ?: continue
                val t = Transform(
                    Vector2f(transform.position),
                    transform.rotation,
                    Vector2f(spriteSize).mul(sprite.size)
                )
                addSprite(sprite.sprite, t, Vector2f(currentRowX, currentRowY))
                currentRowX += 1f
            }
            currentRowY += 1f
        }
    }

    private fun addSprite(sprite: Sprite, transform: Transform, offset: Vector2f) {

        //TODO: Create exception for this
        if (nEntities >= maxSprites || textures.size >= maxTextures) {
            return
        }

        val quad = getQuad(offset)

        addAttributeData(
            POSITION_INDEX,
            quad.tl.x, quad.tl.y,
            quad.bl.x, quad.bl.y,
            quad.br.x, quad.br.y,
            quad.tr.x, quad.tr.y,
            perVertex = false
        )

        addAttributeData(TRANSLATION_INDEX, transform.position.x, transform.position.y)
        addAttributeData(ROTATION_INDEX, transform.rotation)
        addAttributeData(SCALE_INDEX, transform.scale.x, transform.scale.y)
        addAttributeData(
            TEXTURE_COORDS_INDEX,
            sprite.startTextureCoordinates.x,   // TL
            sprite.startTextureCoordinates.y,

            sprite.startTextureCoordinates.x,   // BL
            sprite.endTextureCoordinates.y,

            sprite.endTextureCoordinates.x,     // BR
            sprite.endTextureCoordinates.y,

            sprite.endTextureCoordinates.x,     // TR
            sprite.startTextureCoordinates.y,
            perVertex = false
        )

        var textureIndex = textures.indexOf(sprite.texture)
        if (textureIndex < 0) {
            textures.add(sprite.texture)
            textureIndex = textures.size - 1
        }
        addAttributeData(TEXTURE_INDEX, textureIndex)




        val indexOffset = nEntities * 4
        addIndexData(
            0 + indexOffset,
            1 + indexOffset,
            2 + indexOffset,
            2 + indexOffset,
            3 + indexOffset,
            0 + indexOffset
        )
        nEntities += 1
    }

    override fun clear() {
        super.clear()
        textures.clear()
    }

    fun isTextureFull(): Boolean {
        return textures.size == maxTextures
    }

    fun hasTexture(texture: Texture): Boolean {
        return textures.find { it.id == texture.id } != null
    }

    fun getTextureSlots(): Int {
        return textures.size
    }

}