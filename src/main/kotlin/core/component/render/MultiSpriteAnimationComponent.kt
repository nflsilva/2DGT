package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Transform
import render.model.MultiSprite
import java.util.*

class MultiSpriteAnimationComponent(
    entityId: UUID,
    private val rows: Int,
    private val columns: Int
) : BaseComponent(entityId) {

    private data class AnimationKeyframe(
        val multiSprite: MultiSprite,
        val duration: Double
    )

    private var currentState: String? = null
    private var currentKeyframeIndex: Int = 0
    private var currentKeyframeElapsedTime: Double = 0.0
    private val keyframesByState: MutableMap<String, MutableList<AnimationKeyframe>> = mutableMapOf()

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {

        if (currentState == null) return
        val currentStateKeyframes = keyframesByState[currentState] ?: return

        val currentKeyframe = currentStateKeyframes[currentKeyframeIndex % currentStateKeyframes.size]
        val sprites = currentKeyframe.multiSprite

        val spriteSize = Vector2f(entity.transform.scale)
            .div(Vector2f(columns.toFloat(), rows.toFloat()))

        for(r in 0 until rows){
            for(c in 0 until columns){
                val sprite = sprites.getSprite(r, c) ?: continue
                val drawOffset = Vector2f(c.toFloat(), r.toFloat()).mul(spriteSize)
                val transform = Transform(
                    Vector2f(entity.transform.position).add(drawOffset),
                    entity.transform.rotation,
                    spriteSize
                )
                context.graphics.render(sprite, transform)
            }
        }

        currentKeyframeElapsedTime += context.elapsedTime
        if (currentKeyframe.duration < currentKeyframeElapsedTime) {
            currentKeyframeIndex++
            currentKeyframeElapsedTime = 0.0
        }

    }

    fun addAnimationKeyframe(state: String,
                             multiSprite: MultiSprite,
                             duration: Double) {

        if (state !in keyframesByState.keys) {
            keyframesByState[state] = mutableListOf()
        }

        keyframesByState[state]?.add(AnimationKeyframe(multiSprite, duration))
    }

    fun setState(state: String) {
        currentState = state
    }

}