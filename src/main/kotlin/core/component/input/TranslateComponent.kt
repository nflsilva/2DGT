package core.component.input

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import ui.dto.InputStateData
import java.util.*

class TranslateComponent(entityId: UUID, private val speed: Float = 10F) : BaseComponent(entityId) {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {

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