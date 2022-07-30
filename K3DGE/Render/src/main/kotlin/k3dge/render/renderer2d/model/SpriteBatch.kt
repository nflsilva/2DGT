package k3dge.render.renderer2d.model

import k3dge.render.common.dto.TransformData
import k3dge.render.common.model.Texture
import k3dge.render.renderer2d.dto.Sprite
import org.joml.Vector2f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class SpriteBatch(private val maxSprites: Int,
                  private val maxTextures: Int):
    EntityBatch(maxSprites, 4, 6) {

    private val textures: MutableList<Texture> = mutableListOf()

    companion object {
        const val POSITION_INDEX = 0
        const val SIZE_INDEX = 1
        const val TRANSLATION_INDEX = 2
        const val ROTATION_INDEX = 3
        const val SCALE_INDEX = 4
        const val TEXTURE_COORDS_INDEX = 5
        const val TEXTURE_INDEX = 6
    }

    init {
        addAttributeBuffer(POSITION_INDEX, 2)
        addAttributeBuffer(SIZE_INDEX, 1)
        addAttributeBuffer(TRANSLATION_INDEX, 2)
        addAttributeBuffer(ROTATION_INDEX, 1)
        addAttributeBuffer(SCALE_INDEX, 2)
        addAttributeBuffer(TEXTURE_COORDS_INDEX, 2)
        addAttributeBuffer(TEXTURE_INDEX, 1)
    }

    override fun bind(){
        super.bind()
        for(i in 0 until textures.size) {
            textures[i].bind(i)
        }
    }
    override fun unbind(){
        super.unbind()
        glBindVertexArray(0)
    }

    fun addSprite(sprite: Sprite, transform: TransformData){

        //TODO: Create exception for this
        if(nEntities >= maxSprites || textures.size >= maxTextures) { return }

        val size = sprite.spriteSize.value.toFloat()
        val tl = Vector2f(0f, size)
        val bl = Vector2f(0f, 0f)
        val br = Vector2f(size, 0f)
        val tr = Vector2f(size, size)

        addAttributeData(POSITION_INDEX,
            tl.x, tl.y,
            bl.x, bl.y,
            br.x, br.y,
            tr.x, tr.y,
            perVertex = false)

        addAttributeData(SIZE_INDEX, size)
        addAttributeData(TRANSLATION_INDEX, transform.position.x, transform.position.y)
        addAttributeData(ROTATION_INDEX, transform.rotation.z)
        addAttributeData(SCALE_INDEX, transform.scale.x, transform.scale.y)
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
            perVertex =  false)

        var textureIndex = textures.indexOf(sprite.texture).toFloat()
        if(textureIndex < 0){
            textures.add(sprite.texture)
            textureIndex = textures.size.toFloat()
        }

        addAttributeData(TEXTURE_INDEX, textureIndex)

        val indexOffset = nEntities * 4
        addIndexData(
            0 + indexOffset,
            1 + indexOffset,
            2 + indexOffset,
            2 + indexOffset,
            3 + indexOffset,
            0 + indexOffset)
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