package k3dge.render.renderergui.shader

import k3dge.render.common.dto.ShaderUniformData
import k3dge.render.common.model.Shader
import k3dge.tools.ResourceManager

class GuiShader: Shader(ResourceManager.loadShaderSourceFromFile("/shader/gui/vertex.glsl")!!,
    ResourceManager.loadShaderSourceFromFile("/shader/gui/fragment.glsl")!!) {

    init {
        bindAttributes()
        createUniforms()
    }
    override fun bindAttributes() {
        bindAttribute(0, POSITION_ATTRIBUTE);
    }
    override fun createUniforms() {
        addUniform(MODEL_MATRIX_UNIFORM)
        addUniform(TEXTURE_0_UNIFORM)
    }
    override fun updateUniforms(data: ShaderUniformData) {
        setUniformMatrix4f(MODEL_MATRIX_UNIFORM, data.modelMatrix)
        setUniformi(TEXTURE_0_UNIFORM, 0)
    }

    companion object {
        private const val POSITION_ATTRIBUTE = "in_position"
        private const val MODEL_MATRIX_UNIFORM = "in_modelMatrix"

        private const val TEXTURE_0_UNIFORM = "texture0"
    }
}