package render.shader

import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import render.model.Shader
import render.model.ShaderUniforms

class ParticleShader : Shader(VERTEX_SHADER, FRAGMENT_SHADER) {

    init {
        bindAttributes()
        createUniforms()
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
    }

    override fun bindAttributes() {
        bindAttribute(0, POSITION_ATTRIBUTE)
        bindAttribute(1, SIZE_ATTRIBUTE)
        bindAttribute(2, TYPE_ATTRIBUTE)
        bindAttribute(3, COLOR_ATTRIBUTE)
    }

    override fun createUniforms() {
        addUniform(PROJECTION_MATRIX_UNIFORM)
    }

    override fun updateUniforms(data: ShaderUniforms) {
        setUniformMatrix4f(PROJECTION_MATRIX_UNIFORM, data.projectionMatrix)
    }

    companion object {
        private const val VERTEX_SHADER = "/shader/2d/particle/vertex.glsl"
        private const val FRAGMENT_SHADER = "/shader/2d/particle/fragment.glsl"

        private const val POSITION_ATTRIBUTE = "in_position"
        private const val SIZE_ATTRIBUTE = "in_size"
        private const val TYPE_ATTRIBUTE = "in_type"
        private const val COLOR_ATTRIBUTE = "in_color"

        private const val PROJECTION_MATRIX_UNIFORM = "in_projectionMatrix"
    }
}