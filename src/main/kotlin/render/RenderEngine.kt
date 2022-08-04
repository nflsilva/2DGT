package render

import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30
import org.lwjgl.opengl.GL30.glClearColor
import render.dto.Particle
import render.dto.Shape
import render.dto.Sprite
import render.model.*
import render.shader.ParticleShader
import render.shader.ShapeShader
import render.shader.SpriteShader
import core.EngineConfiguration

class RenderEngine(private val configuration: EngineConfiguration) {

    private var backgroundColor: Color = Color(0.0f)

    private lateinit var spriteBatches: MutableList<SpriteBatch>
    private lateinit var spriteShader: SpriteShader

    private lateinit var particleBatches: MutableList<ParticlesBatch>
    private lateinit var particleShader: ParticleShader

    private lateinit var shapeBatches: MutableList<ShapesBatch>
    private lateinit var shapeShader: ShapeShader

    private var maxTextureSlots: Int = 0

    private var zoom = 0.0F
    private val bottom = 0.0F
    private val left = 0.0F
    private val top = configuration.resolutionHeight.toFloat() * (1.0F - zoom)
    private val right = configuration.resolutionWidth.toFloat() * (1.0F - zoom)

    companion object {
        const val DEFAULT_BATCH_SIZE: Int = 10000
        const val DEFAULT_SCREEN_RENDER_MARGINS: Int = 100
    }

    fun onStart() {

        val mtsb = BufferUtils.createIntBuffer(1)
        glGetIntegerv(GL30.GL_MAX_TEXTURE_IMAGE_UNITS, mtsb)
        maxTextureSlots = mtsb.get()

        spriteBatches = mutableListOf()
        spriteShader = SpriteShader()

        particleShader = ParticleShader()
        particleBatches = mutableListOf()

        shapeShader = ShapeShader()
        shapeBatches = mutableListOf()
    }
    fun onFrame() {

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a)

        glViewport(0, 0, configuration.resolutionWidth, configuration.resolutionHeight)
        glEnable(GL_CULL_FACE)
        glCullFace(GL_BACK)
        draw()

    }
    fun onUpdate() {
        spriteBatches.forEach { it.clear() }
        particleBatches.forEach { it.clear() }
        shapeBatches.forEach { it.clear() }
    }
    fun onCleanUp() {
        for (batch in spriteBatches) {
            batch.cleanUp()
        }
    }

    fun setBackgroundColor(color: Color) {
        backgroundColor = color
    }
    fun render(sprite: Sprite, transform: Transform) {
        if (isVisible(transform, sprite.size.value)) {
            addToSuitableSpriteBatch(sprite, transform)
        }
    }
    fun render(particle: Particle, transform: Transform) {
        if (isVisible(transform, particle.size)) {
            addToSuitableParticleBatch(particle, transform)
        }
    }
    fun render(shape: Shape, transform: Transform) {
        if (isVisible(transform, 0f)) {
            addToSuitableShapeBatch(shape, transform)
        }
    }

    private fun isVisible(transform: Transform, size: Float): Boolean {
        return transform.position.x > left - DEFAULT_SCREEN_RENDER_MARGINS &&
                transform.position.x + size < right + DEFAULT_SCREEN_RENDER_MARGINS &&
                transform.position.y < top + DEFAULT_SCREEN_RENDER_MARGINS &&
                transform.position.y + size > bottom - DEFAULT_SCREEN_RENDER_MARGINS
    }
    private fun addToSuitableSpriteBatch(data: Sprite, transform: Transform) {
        var suitableBatch: SpriteBatch? = null
        for (batch in spriteBatches) {
            if (batch.isFull()) {
                continue
            }
            if (batch.hasTexture(data.texture) || !batch.isTextureFull()) {
                suitableBatch = batch
                break
            }
        }

        if (suitableBatch == null) {
            suitableBatch = SpriteBatch(DEFAULT_BATCH_SIZE, maxTextureSlots)
            spriteBatches.add(suitableBatch)
        }

        suitableBatch.addSprite(data, transform)
    }
    private fun addToSuitableParticleBatch(data: Particle, transform: Transform) {
        var suitableBatch: ParticlesBatch? = null
        for (batch in particleBatches) {
            if (batch.isFull()) {
                continue
            }
            suitableBatch = batch
            break
        }

        if (suitableBatch == null) {
            suitableBatch = ParticlesBatch(DEFAULT_BATCH_SIZE)
            particleBatches.add(suitableBatch)
        }

        suitableBatch.addParticle(data, transform)
    }
    private fun addToSuitableShapeBatch(data: Shape, transform: Transform) {
        var suitableBatch: ShapesBatch? = null
        for (batch in shapeBatches) {
            if (batch.isFull()) {
                continue
            }
            suitableBatch = batch
            break
        }


        if (suitableBatch == null) {
            suitableBatch = ShapesBatch(DEFAULT_BATCH_SIZE)
            shapeBatches.add(suitableBatch)
        }

        suitableBatch.addShape(data, transform)
    }

    //TODO: move this method into Shader?
    private fun prepareShader(shader: Shader, uniformData: ShaderUniforms) {
        shader.bind()
        shader.updateUniforms(uniformData)
    }
    private fun draw() {
        val projectionMatrix: Matrix4f = Matrix4f().setOrtho2D(left, right, bottom, top)
        drawParticles(projectionMatrix)
        drawSprites(projectionMatrix)
        drawShapes(projectionMatrix)
    }
    private fun drawSprites(projectionMatrix: Matrix4f) {
        for (batch in spriteBatches) {
            batch.bind()
            prepareShader(
                spriteShader,
                ShaderUniforms(
                    projectionMatrix = projectionMatrix,
                    textureSlots = batch.getTextureSlots()
                )
            )
            glDrawElements(GL_TRIANGLES, batch.nIndexes, GL_UNSIGNED_INT, 0)
            batch.unbind()
        }
    }
    private fun drawParticles(projectionMatrix: Matrix4f) {
        for (batch in particleBatches) {
            batch.bind()
            prepareShader(
                particleShader,
                ShaderUniforms(projectionMatrix = projectionMatrix)
            )
            glDrawElements(GL_POINTS, batch.nIndexes, GL_UNSIGNED_INT, 0)
            batch.unbind()
        }
    }
    private fun drawShapes(projectionMatrix: Matrix4f) {
        for (batch in shapeBatches) {
            batch.bind()
            prepareShader(
                shapeShader,
                ShaderUniforms(projectionMatrix = projectionMatrix)
            )
            glDrawElements(GL_TRIANGLES, batch.nIndexes, GL_UNSIGNED_INT, 0)
            batch.unbind()
        }
    }

}