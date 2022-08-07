package render.batch

import org.joml.Vector2f
import org.lwjgl.opengl.GL30.glBindVertexArray
import render.dto.Sprite
import render.model.Texture
import render.dto.Transform

class SpriteBatch(
    private val maxSprites: Int,
    private val maxTextures: Int
) :
    BaseBatch(maxSprites, 4, 6) {

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
        glBindVertexArray(0)
    }

    fun addSprite(sprite: Sprite, transform: Transform) {

        //TODO: Create exception for this
        if (nEntities >= maxSprites || textures.size >= maxTextures) {
            return
        }

        val tl = Vector2f(0f, 1f)
        val bl = Vector2f(0f, 0f)
        val br = Vector2f(1f, 0f)
        val tr = Vector2f(1f, 1f)

        addAttributeData(
            POSITION_INDEX,
            tl.x, tl.y,
            bl.x, bl.y,
            br.x, br.y,
            tr.x, tr.y,
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
            textureIndex = textures.size
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
        return textures.contains(texture)
    }

    fun getTextureSlots(): Int {
        return textures.size
    }

}