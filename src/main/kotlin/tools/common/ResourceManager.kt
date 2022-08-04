package tools.common

import org.lwjgl.BufferUtils
import org.lwjgl.stb.STBImage.stbi_load
import tools.common.dto.ShaderData
import tools.common.dto.ImageData
import tools.common.exception.ErrorLoadingResourceException
import tools.common.exception.ResourceNotFoundException
import java.net.URL
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
        stbi_load(resourceUrl.path, width, height, components, 4)?.let { data ->
            val textureData = ImageData(width.get(), height.get(), components.get(), data)
            textures[fileName] = textureData
            return textureData
        }
        throw ErrorLoadingResourceException(fileName)
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