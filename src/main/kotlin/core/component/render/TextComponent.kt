package core.component.render

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Transform
import render.model.BitmapFont
import java.util.*

class TextComponent(
    entityId: UUID,
    var drawOffset: Vector2f,
    var text: String,
    val fontBitmap: BitmapFont,
    val fontSize: Float
    ): BaseComponent(entityId) {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {

        for((i, c) in text.withIndex()){
            val char = fontBitmap.getCharacter(c)

            val transform = Transform(
                Vector2f(entity.transform.position)
                    .add(drawOffset)
                    .add(Vector2f(fontSize * i, 0f)),
                entity.transform.rotation,
                Vector2f(fontSize)//.mul(entity.transform.scale)
            )
            context.graphics.render(char, transform)

        }

    }

}