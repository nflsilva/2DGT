package render.model

import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import tools.common.Log
import tools.common.ResourceManager
import tools.common.dto.ShaderData

abstract class Shader(vertexData: ShaderData, fragmentData: ShaderData) {

    private val id: Int = glCreateProgram()
    private val shaders: MutableList<Int> = mutableListOf()
    private val uniforms: MutableMap<String, Int> = mutableMapOf()

    constructor(vertexResource: String, fragmentResource: String) :
            this(
                ResourceManager.loadShaderSourceFromFile(vertexResource),
                ResourceManager.loadShaderSourceFromFile(fragmentResource)
            )

    init {
        addShader(vertexData.sourceCode, GL_VERTEX_SHADER)
        addShader(fragmentData.sourceCode, GL_FRAGMENT_SHADER)
        linkProgram()
    }

    fun cleanUp() {

    }

    fun bind() {
        glUseProgram(id)
    }

    fun unbind() {
        glUseProgram(0)
    }

    fun bindAttribute(attribute: Int, variableName: String) {
        glBindAttribLocation(id, attribute, variableName);
    }

    fun addUniform(name: String) {
        if (uniforms.containsKey(name)) return
        val location: Int = glGetUniformLocation(id, name)
        if (location == GL_INVALID_VALUE || location == GL_INVALID_OPERATION || location == GL_INVALID_OPERATION) {
            Log.e("Failed to create \"$name\" uniform.");
            return
        }
        uniforms[name] = location
    }

    fun setUniformi(name: String, value: Int) {
        uniforms[name]?.let {
            glUniform1i(it, value)
        }
    }
    fun setUniformiv(name: String, value: List<Int>) {
        uniforms[name]?.let {
            glUniform1iv(it, value.toIntArray())
        }
    }
    fun setUniformf(name: String, value: Float) {
        uniforms[name]?.let {
            glUniform1f(it, value)
        }
    }
    fun setUniform3f(name: String, value: Vector3f) {
        uniforms[name]?.let {
            glUniform3f(it, value.x, value.y, value.z)
        }
    }
    fun setUniform4f(name: String, value: Vector4f) {
        uniforms[name]?.let {
            glUniform4f(it, value.x, value.y, value.z, value.w)
        }
    }
    fun setUniformMatrix4f(name: String, value: Matrix4f) {
        uniforms[name]?.let {
            val data = BufferUtils.createFloatBuffer(4 * 4)
            glUniformMatrix4fv(it, false, value.get(data))
        }
    }

    abstract fun updateUniforms(data: ShaderUniforms)
    protected abstract fun bindAttributes()
    protected abstract fun createUniforms()

    private fun addShader(sourceCode: String, type: Int) {

        val shader = glCreateShader(type)
        if (shader == 0) {
            Log.e("Error creating shader")
            return
        }
        glShaderSource(shader, sourceCode)

        val compileSuccess = BufferUtils.createIntBuffer(1)
        glCompileShader(shader);
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileSuccess)
        if (compileSuccess.get() != GL_TRUE) {
            val message = glGetShaderInfoLog(shader)
            Log.e(message)
            return
        }

        glAttachShader(id, shader)
        shaders.add(shader)
    }

    private fun linkProgram() {

        var success = BufferUtils.createIntBuffer(1)
        glLinkProgram(id);
        glGetProgramiv(id, GL_LINK_STATUS, success)
        if (success.get() != GL_TRUE) {
            val message = glGetProgramInfoLog(id)
            Log.e(message)
            return
        }

        success = BufferUtils.createIntBuffer(1)
        glValidateProgram(id);
        glGetProgramiv(id, GL_VALIDATE_STATUS, success)
        if (success.get() != GL_TRUE) {
            val message = glGetProgramInfoLog(id)
            Log.e(message)
            return
        }
    }

}