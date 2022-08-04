package core.entity.component

import core.common.Component
import core.common.dto.UpdateContext
import ui.dto.InputStateData

class TranslateComponent(private val speed: Float = 10F) : Component() {

    init {
        setUpdateObserver { context -> onUpdate(context) }
    }

    private fun onUpdate(context: UpdateContext) {
        context.entity?.let { entity ->

            val dd = speed * context.elapsedTime.toFloat()

            if (context.input.isKeyPressed(InputStateData.KEY_A)) {
                entity.transform.translate(-dd, 0f)
            } else if (context.input.isKeyPressed(InputStateData.KEY_D)) {
                entity.transform.translate(dd, 0f)
            }

            if (context.input.isKeyPressed(InputStateData.KEY_W)) {
                entity.transform.translate(0f, dd)
            } else if (context.input.isKeyPressed(InputStateData.KEY_S)) {
                entity.transform.translate(0f, -dd)
            }

        }
    }
}