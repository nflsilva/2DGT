import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.input.TranslateComponent
import core.component.render.ParticleComponent
import core.component.render.SpriteAnimationComponent
import org.joml.Vector2f
import render.dto.Color
import render.dto.Transform
import render.model.SpriteAtlas
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = AnimatedSprites(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class AnimatedSprites(private val engine: CoreEngine) : CoreEngineDelegate {

    private val animatedSprite = BaseEntity(Transform(Vector2f(0f, 0f), 0f, Vector2f(64f, 64f)))
    private val animComp = SpriteAnimationComponent(animatedSprite.uid)

    override fun onStart() {

        val atlas = SpriteAtlas(
            "/texture/dungeon.png",
            9,
            28).apply {

            setSprite("walking0", 6, 0)
            setSprite("walking1", 6, 1)
            setSprite("walking2", 6, 2)
            setSprite("walking3", 6, 3)
            setSprite("walking4", 6, 4)
            setSprite("walking5", 6, 5)

            setSprite("idle0", 5, 0)
            setSprite("idle1", 5, 1)
            setSprite("idle2", 5, 2)
            setSprite("idle3", 5, 3)
            setSprite("idle4", 5, 4)
            setSprite("idle5", 5, 5)

            setSprite("idle0", 5, 0)
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
        animatedSprite.addComponent(TranslateComponent(animatedSprite.uid, 500F))
        engine.addEntity(animatedSprite)

        val particleEntity = BaseEntity(Transform(
            Vector2f(10f, 10f),
            0.0f,
            Vector2f(1f, 1f)))
        val particleComponent = ParticleComponent(particleEntity.uid, 0, 15f, Color(1.0f, 0.0f, 0.0f, 1.0f))
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