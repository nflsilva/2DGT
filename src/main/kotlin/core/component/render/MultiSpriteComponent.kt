package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Sprite
import render.model.MultiSprite
import java.util.*

class MultiSpriteComponent(entityId: UUID, rows: Int, columns: Int) : BaseComponent(entityId) {

    private val multiSprite = MultiSprite(rows, columns)

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        context.graphics.render(multiSprite, entity.transform)
    }

    fun addSprite(row: Int, column: Int, size: Vector2f, sprite: Sprite){
        multiSprite.addSprite(row, column, size, sprite)
    }

}