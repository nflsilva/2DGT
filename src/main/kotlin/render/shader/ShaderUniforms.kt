package render.shader

import org.joml.Matrix4f

class ShaderUniforms(projectionMatrix: Matrix4f? = null, textureSlots: Int? = null) {

    val projectionMatrix: Matrix4f
    val textureSlots: Int

    init {
        this.projectionMatrix = projectionMatrix ?: Matrix4f().identity()
        this.textureSlots = textureSlots ?: 0
    }
}