package core.component

import core.BaseEntity
import core.dto.UpdateContext
import render.dto.Sprite

class SpriteAnimationComponent() : Component() {

    private data class AnimationKeyframe(
        val sprite: Sprite,
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

        if (currentState == null) {
            return
        }

        val currentStateKeyframes = keyframesByState[currentState] ?: return
        val currentKeyframe = currentStateKeyframes[currentKeyframeIndex % currentStateKeyframes.size]

        context.graphics.render(currentKeyframe.sprite, entity.transform)

        currentKeyframeElapsedTime += context.elapsedTime
        if (currentKeyframe.duration < currentKeyframeElapsedTime) {
            currentKeyframeIndex++
            currentKeyframeElapsedTime = 0.0
        }

    }

    fun addAnimationKeyframe(state: String, sprite: Sprite, duration: Double) {
        if (state !in keyframesByState.keys) {
            keyframesByState[state] = mutableListOf()
        }
        keyframesByState[state]?.add(AnimationKeyframe(sprite, duration))
    }

    fun setState(state: String) {
        currentState = state
    }

}