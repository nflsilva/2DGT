package render.model

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.*
import java.nio.Buffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

open class EntityBatch(
    private val maxEntities: Int,
    private val nVerticesPerEntity: Int,
    private val nIndexesPerEntity: Int
) {

    private data class Buffers(val vbo: Int, val buffer: Buffer)

    var nEntities: Int = 0
    val nIndexes: Int
        get() = this.nEntities * nIndexesPerEntity

    private val vao: Int = glGenVertexArrays()
    private val attributes: MutableMap<Int, Buffers> = mutableMapOf()

    private val indexesVbo: Int
    private val indices: IntBuffer

    init {
        glBindVertexArray(vao)
        with(initIndexBuffer()) { indexesVbo = vbo; indices = buffer as IntBuffer }
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    open fun bind() {

        attributes.forEach { attribute ->
            bindAttributeBuffer(attribute.value.vbo, attribute.value.buffer.flip())
        }

        bindIndexBuffer(indexesVbo, indices.flip())

        glBindVertexArray(vao)
        for (i in 0 until attributes.size) {
            glEnableVertexAttribArray(i)
        }

    }
    open fun unbind() {
        for (i in 0 until attributes.size) {
            glDisableVertexAttribArray(i)
        }
        glBindVertexArray(0)
    }

    fun addIntAttributeBuffer(index: Int, size: Int) {
        glBindVertexArray(vao)
        val vbo = glGenBuffers()
        val buffer = BufferUtils.createIntBuffer(maxEntities * nVerticesPerEntity * size)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        glVertexAttribIPointer(index, size, GL_UNSIGNED_INT, 0, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        attributes[index] = Buffers(vbo, buffer)
    }
    fun addFloatAttributeBuffer(index: Int, size: Int) {
        glBindVertexArray(vao)
        val vbo = glGenBuffers()
        val buffer = BufferUtils.createFloatBuffer(maxEntities * nVerticesPerEntity * size)
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        glVertexAttribPointer(index, size, GL_FLOAT, false, 0, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        attributes[index] = Buffers(vbo, buffer)
    }

    fun addAttributeData(index: Int, vararg data: Int, perVertex: Boolean = true) {
        val adds = if (perVertex) nVerticesPerEntity else 1
        val buffer = attributes[index]?.buffer as? IntBuffer ?: return
        for (i in 0 until adds) {
            data.forEach { value ->
                buffer.put(value)
            }
        }
    }
    fun addAttributeData(index: Int, vararg data: Float, perVertex: Boolean = true) {
        val adds = if (perVertex) nVerticesPerEntity else 1
        val buffer = attributes[index]?.buffer as? FloatBuffer ?: return
        for (i in 0 until adds) {
            data.forEach { value ->
                buffer.put(value)
            }
        }
    }
    fun addIndexData(vararg data: Int) {
        data.forEach { value ->
            indices.put(value)
        }
    }

    open fun clear() {
        attributes.values.forEach { it.buffer.clear() }
        indices.clear()
        nEntities = 0
    }
    fun isFull(): Boolean {
        return nEntities == maxEntities
    }
    fun cleanUp() {
        glBindVertexArray(0)
        glDeleteVertexArrays(vao)
        glDeleteBuffers(attributes.keys.toIntArray())
    }

    private fun initIndexBuffer(): Buffers {
        val vbo = glGenBuffers()
        val buffer = BufferUtils.createIntBuffer(maxEntities * nIndexesPerEntity)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW)
        return Buffers(vbo, buffer)
    }
    private fun bindAttributeBuffer(vbo: Int, buffer: Buffer) {
        // TODO: Refactor this IntBuffer / FloatBuffer mess
        glBindBuffer(GL_ARRAY_BUFFER, vbo)
        val intBuffer = buffer as? IntBuffer
        if (intBuffer != null) {
            glBufferSubData(GL_ARRAY_BUFFER, 0, intBuffer)
        } else {
            glBufferSubData(GL_ARRAY_BUFFER, 0, buffer as FloatBuffer)
        }
    }
    private fun bindIndexBuffer(vbo: Int, buffer: Buffer) {
        val intBuffer = buffer as? IntBuffer ?: return
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo)
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, intBuffer)
    }

}