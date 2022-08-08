package core.component

import core.BaseEntity
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Transform
import render.model.BitmapFont

class TextComponent(
    var drawOffset: Vector2f,
    var text: String,
    val fontBitmap: BitmapFont
    ): BaseComponent() {

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        val sizePerChar = Vector2f(text.length.toFloat())
            .div(entity.transform.scale)

        for((i, c) in text.withIndex()){
            val char = fontBitmap.getCharacter(c)

            val transform = Transform(
                Vector2f(entity.transform.position)
                    .add(drawOffset)
                    .add(Vector2f(sizePerChar.x * i, 0f)),
                entity.transform.rotation,
                sizePerChar
            )
            context.graphics.render(char, transform)

        }



    }

}