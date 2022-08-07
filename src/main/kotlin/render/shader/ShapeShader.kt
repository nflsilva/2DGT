package render.shader

class ShapeShader : BaseShader(VERTEX_SHADER, FRAGMENT_SHADER) {

    init {
        bindAttributes()
        createUniforms()
    }

    override fun bindAttributes() {
        bindAttribute(0, POSITION_ATTRIBUTE);
        bindAttribute(1, TRANSLATION_ATTRIBUTE);
        bindAttribute(2, ROTATION_ATTRIBUTE);
        bindAttribute(3, SCALE_ATTRIBUTE);
        bindAttribute(4, TYPE_ATTRIBUTE);
        bindAttribute(5, COLOR_ATTRIBUTE);
    }

    override fun createUniforms() {
        addUniform(PROJECTION_MATRIX_UNIFORM)
    }

    override fun updateUniforms(data: ShaderUniforms) {
        setUniformMatrix4f(PROJECTION_MATRIX_UNIFORM, data.projectionMatrix)
    }

    companion object {
        private const val VERTEX_SHADER = "/shader/shape/vertex.glsl"
        private const val FRAGMENT_SHADER = "/shader/shape/fragment.glsl"

        private const val POSITION_ATTRIBUTE = "in_position"
        private const val TRANSLATION_ATTRIBUTE = "in_translation"
        private const val ROTATION_ATTRIBUTE = "in_rotation"
        private const val SCALE_ATTRIBUTE = "in_scale"
        private const val TYPE_ATTRIBUTE = "in_type"
        private const val COLOR_ATTRIBUTE = "in_color"

        private const val PROJECTION_MATRIX_UNIFORM = "in_projectionMatrix"

    }
}