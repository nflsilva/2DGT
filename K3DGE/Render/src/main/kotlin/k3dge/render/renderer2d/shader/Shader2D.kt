package k3dge.render.renderer2d.shader

import k3dge.render.common.dto.ShaderUniformData
import k3dge.render.common.shader.Shader
import k3dge.tools.ResourceManager

class Shader2D: Shader(VERTEX_SHADER, FRAGMENT_SHADER) {

    init {
        bindAttributes()
        createUniforms()
    }
    override fun bindAttributes() {
        bindAttribute(0, POSITION_ATTRIBUTE);
        bindAttribute(1, ROTATION_ATTRIBUTE);
        bindAttribute(2, SCALE_ATTRIBUTE);
        bindAttribute(3, TEXTCOORDS_ATTRIBUTE);
        bindAttribute(4, TEXTINDEX_ATTRIBUTE);
    }
    override fun createUniforms() {
        addUniform(PROJECTION_MATRIX_UNIFORM)
        addUniform(TEXTURE_SLOTS_UNIFORM)
    }
    override fun updateUniforms(data: ShaderUniformData) {
        setUniformMatrix4f(PROJECTION_MATRIX_UNIFORM, data.projectionMatrix)

        val samplers = mutableListOf<Int>()
        for(i in 0 until data.textureSlots){ samplers.add(i) }
        setUniformiv(TEXTURE_SLOTS_UNIFORM, samplers)
    }

    companion object {
        private const val VERTEX_SHADER = "/shader/2d/static/vertex.glsl"
        private const val FRAGMENT_SHADER = "/shader/2d/static/fragment.glsl"

        private const val POSITION_ATTRIBUTE = "in_position"
        private const val ROTATION_ATTRIBUTE = "in_rotation"
        private const val SCALE_ATTRIBUTE = "in_scale"
        private const val TEXTCOORDS_ATTRIBUTE = "in_textureCoords"
        private const val TEXTINDEX_ATTRIBUTE = "in_textureIndex"
        private const val PROJECTION_MATRIX_UNIFORM = "in_projectionMatrix"

        private const val TEXTURE_SLOTS_UNIFORM = "in_samplers"
    }
}