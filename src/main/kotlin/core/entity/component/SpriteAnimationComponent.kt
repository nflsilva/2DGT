package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
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
        setUpdateObserver { context -> onUpdate(context) }
        setCleanupObserver { cleanUp() }
    }

    private fun onUpdate(context: UpdateContext) {

        if (currentState == null) {
            return
        }

        val currentStateKeyframes = keyframesByState[currentState] ?: return
        val currentKeyframe = currentStateKeyframes[currentKeyframeIndex % currentStateKeyframes.size]

        context.entity?.let { entity ->
            context.graphics.render(currentKeyframe.sprite, entity.transform)
        }

        currentKeyframeElapsedTime += context.elapsedTime
        if (currentKeyframe.duration < currentKeyframeElapsedTime) {
            currentKeyframeIndex++
            currentKeyframeElapsedTime = 0.0
        }

    }

    private fun cleanUp() {

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