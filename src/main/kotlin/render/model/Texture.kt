package render.model

import org.lwjgl.opengl.GL13.*
import tools.common.ResourceManager
import tools.common.dto.ImageData
import java.nio.ByteBuffer

class Texture(
    val width: Int,
    val height: Int,
    data: ByteBuffer
) {

    private val id: Int = glGenTextures()

    constructor(textureData: ImageData) : this(textureData.width, textureData.height, textureData.data)
    constructor(resourceName: String) : this(ResourceManager.loadTextureFromFile(resourceName))

    init {
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data)
        glBindTexture(GL_TEXTURE_2D, 0)
    }

    fun cleanUp() {
        glBindTexture(GL_TEXTURE_2D, 0)
        glDeleteTextures(id)
    }

    fun bind(slot: Int) {
        glActiveTexture(GL_TEXTURE0 + slot)
        glBindTexture(GL_TEXTURE_2D, id)
    }

    fun unbind() {
        glBindTexture(GL_TEXTURE_2D, 0)
    }
}