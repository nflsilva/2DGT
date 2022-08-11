package render.batch

import org.joml.Vector2f
import org.lwjgl.opengl.GL11.GL_TEXTURE_2D
import org.lwjgl.opengl.GL11.glBindTexture
import render.dto.Sprite
import render.dto.Transform
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

    fun addSprite(sprite: Sprite, transform: Transform, offset: Vector2f): Boolean {

        if (nEntities >= maxSprites || textures.size >= maxTextures) {
            return true
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

        var textureIndex: Int? = null
        for((i, texture) in textures.withIndex()){
            if(texture.id == sprite.texture.id){
                textureIndex = i
                break
            }
        }
        if(textureIndex == null){
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

        return nEntities >= maxSprites || textures.size >= maxTextures
    }

    override fun clear() {
        super.clear()
        textures.clear()
    }

    override fun isFull(): Boolean {
        val fullEntities = super.isFull()
        return fullEntities || textures.size  >= maxTextures
    }

    fun hasTexture(texture: Texture): Boolean {
        return textures.find { it.id == texture.id } != null
    }

    fun getNumberOfTextures(): Int {
        return textures.size
    }

}