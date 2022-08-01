package programs.d2

import core.CoreEngine
import core.CoreEngineDelegate
import core.entity.Entity2D
import core.entity.component.EntityMoveComponent
import core.entity.component2d.ParticleComponent
import core.entity.component2d.SpriteAnimationComponent
import org.joml.Vector2f
import render.common.dto.ColorData
import render.renderer2d.model.SpriteAtlas
import render.renderer2d.model.SpriteSizeEnum
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = AnimatedSprites(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class AnimatedSprites(private val engine: CoreEngine) : CoreEngineDelegate {

    private val animComp = SpriteAnimationComponent()

    override fun onStart() {

        val animatedSprite = Entity2D(Vector2f(0f, 0f), 0f, Vector2f(10f, 10f))
        val atlas = SpriteAtlas("/texture/dungeon.png", 9, 28).apply {
            setSprite(SpriteSizeEnum.X16,"walking0", 6, 0)
            setSprite(SpriteSizeEnum.X16,"walking1", 6, 1)
            setSprite(SpriteSizeEnum.X16,"walking2", 6, 2)
            setSprite(SpriteSizeEnum.X16,"walking3", 6, 3)
            setSprite(SpriteSizeEnum.X16,"walking4", 6, 4)
            setSprite(SpriteSizeEnum.X16,"walking5", 6, 5)

            setSprite(SpriteSizeEnum.X16,"idle0", 5, 0)
            setSprite(SpriteSizeEnum.X16,"idle1", 5, 1)
            setSprite(SpriteSizeEnum.X16,"idle2", 5, 2)
            setSprite(SpriteSizeEnum.X16,"idle3", 5, 3)
            setSprite(SpriteSizeEnum.X16,"idle4", 5, 4)
            setSprite(SpriteSizeEnum.X16,"idle5", 5, 5)
        }

        animComp.apply {
            addAnimationKeyframe("walking", atlas.getSprite("walking0"), 0.05)
            addAnimationKeyframe("walking", atlas.getSprite("walking1"), 0.05)
            addAnimationKeyframe("walking", atlas.getSprite("walking2"), 0.05)
            addAnimationKeyframe("walking", atlas.getSprite("walking3"), 0.05)
            addAnimationKeyframe("walking", atlas.getSprite("walking4"), 0.05)
            addAnimationKeyframe("walking", atlas.getSprite("walking5"), 0.05)

            addAnimationKeyframe("idle", atlas.getSprite("idle0"), 0.05)
            addAnimationKeyframe("idle", atlas.getSprite("idle1"), 0.05)
            addAnimationKeyframe("idle", atlas.getSprite("idle2"), 0.05)
            addAnimationKeyframe("idle", atlas.getSprite("idle3"), 0.05)
            addAnimationKeyframe("idle", atlas.getSprite("idle4"), 0.05)
            addAnimationKeyframe("idle", atlas.getSprite("idle5"), 0.05)
            setState("idle")
        }

        animatedSprite.addComponent(animComp)
        animatedSprite.addComponent(EntityMoveComponent(500F))
        engine.addEntity(animatedSprite)

        val particleEntity = Entity2D(
            Vector2f(10f, 10f),
            0.0f,
            Vector2f(1f, 1f))
        val particleComponent = ParticleComponent(0, 15f, ColorData(1.0f, 0.0f, 0.0f, 1.0f))
        particleEntity.addComponent(particleComponent)
        engine.addEntity(particleEntity)

    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        if(input.isKeyPressed(InputStateData.KEY_D)){
            animComp.setState("walking")
        }
        else {
            animComp.setState("idle")
        }
    }
    override fun onFrame() {}
    override fun onCleanUp() {}

}