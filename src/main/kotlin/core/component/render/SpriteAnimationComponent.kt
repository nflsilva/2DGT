package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import render.dto.Sprite
import java.util.*

class SpriteAnimationComponent(entityId: UUID, private val loop: Boolean = true) : BaseComponent(entityId) {

    private data class AnimationKeyframe(
        val sprite: Sprite,
        val duration: Double
    )

    var completedState: Boolean = false
    private var currentState: String? = null
    private var currentKeyframeIndex: Int = 0
    private var currentKeyframeElapsedTime: Double = 0.0
    private val keyframesByState: MutableMap<String, MutableList<AnimationKeyframe>> = mutableMapOf()

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {

        if (currentState == null) return

        if(!completedState || loop) {

            val currentStateKeyframes = keyframesByState[currentState] ?: return

            val currentKeyframe = currentStateKeyframes[currentKeyframeIndex]

            context.graphics.render(currentKeyframe.sprite, entity.transform)

            currentKeyframeElapsedTime += context.elapsedTime
            if (currentKeyframe.duration < currentKeyframeElapsedTime) {
                currentKeyframeIndex++
                currentKeyframeElapsedTime = 0.0
            }

            if(currentKeyframeIndex == currentStateKeyframes.size){
                currentKeyframeIndex = 0
                completedState = true
            }
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
        currentKeyframeIndex = 0
        completedState = false
    }

}