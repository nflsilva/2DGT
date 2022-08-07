package core.component

import core.BaseEntity
import core.component.BaseComponent
import core.dto.UpdateContext
import org.joml.Vector2f
import render.dto.Sprite
import render.dto.Transform
import render.model.MultiSprite

class MultiSpriteComponent(rows: Int, columns: Int) : BaseComponent() {

    private val multiSprite = MultiSprite(rows, columns)

    init {
        setUpdateObserver { entity, context -> onUpdate(entity, context) }
    }

    private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
        val spriteSize = Vector2f(entity.transform.scale)
            .div(Vector2f(multiSprite.columns.toFloat(), multiSprite.rows.toFloat()))

        for(r in 0 until multiSprite.rows){
            for(c in 0 until multiSprite.columns){
                val sprite = multiSprite.getSprite(r, c) ?: continue
                val drawOffset = Vector2f(c.toFloat(), r.toFloat()).mul(spriteSize)
                val transform = Transform(
                    Vector2f(entity.transform.position).add(drawOffset),
                    entity.transform.rotation,
                    spriteSize
                )
                context.graphics.render(sprite, transform)
            }
        }
    }

    fun addSprite(row: Int, column: Int, sprite: Sprite){
        multiSprite.addSprite(row, column, sprite)
    }

}