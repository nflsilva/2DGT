package tools.common

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage.*
import tools.common.dto.ShaderData
import tools.common.dto.ImageData
import tools.common.exception.ErrorLoadingResourceException
import tools.common.exception.ResourceNotFoundException
import java.net.URL
import java.nio.Buffer
import java.nio.IntBuffer


object ResourceManager {

    private val textures: MutableMap<String, ImageData> = mutableMapOf()
    private val shaders: MutableMap<String, ShaderData> = mutableMapOf()

    fun loadTextureFromFile(fileName: String): ImageData {
        if(fileName in textures.keys) { return textures[fileName]!! }
        val resourceUrl = getResourceURLFromFile(fileName)
        val width: IntBuffer = BufferUtils.createIntBuffer(1)
        val height: IntBuffer = BufferUtils.createIntBuffer(1)
        val components: IntBuffer = BufferUtils.createIntBuffer(1)

        val data = resourceUrl.readBytes()
        val buffer = BufferUtils.createByteBuffer(data.count())
        buffer.put(data)

        stbi_load_from_memory(buffer.flip(), width, height, components, STBI_rgb_alpha)?.let {
            val textureData = ImageData(width.get(), height.get(), components.get(), it)
            println(textureData)
            textures[fileName] = textureData
            return textureData
        }

        throw ErrorLoadingResourceException(fileName + " R: " + stbi_failure_reason())
    }

    fun loadShaderSourceFromFile(fileName: String): ShaderData {
        if(fileName in shaders.keys) { return shaders[fileName]!! }
        val resourceUrl = getResourceURLFromFile(fileName)
        val shaderData = ShaderData(resourceUrl.readText())
        shaders[fileName] = shaderData
        return shaderData
    }

    private fun getResourceURLFromFile(fileName: String): URL {
        object{}::class.java.getResource(fileName)?.let { url ->
            var resourcePath = url.path
            //Why do I need this hack in windows?
            //resourcePath = resourcePath.drop(1)
            return url
        }
        throw ResourceNotFoundException(fileName)
    }
}