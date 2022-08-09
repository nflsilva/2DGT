import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.render.BackgroundColorComponent
import core.component.render.MultiSpriteAnimationComponent
import core.component.render.SpriteAnimationComponent
import org.joml.Vector2f
import render.dto.Transform
import render.model.MultiSprite
import render.model.SpriteAtlas
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = MultiSpriteExample(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class MultiSpriteExample(private val engine: CoreEngine) : CoreEngineDelegate {


    override fun onStart() {

        // Setup atlas
        val atlas = SpriteAtlas(
            "/texture/dungeon.png",
            9,
            28,
            SpriteAtlas.SpriteSize.X16)

        // Setup mob sprites
        for(nMob in 0 until 8){
            atlas.apply {
                setSprite("mob${nMob}", 1, nMob + 6)
            }
        }

        // Setup door sprites
        for(nDoor in 0 until 14) {
            atlas.apply {
                setSprite("door${nDoor}_tl", 7, nDoor * 2)
                setSprite("door${nDoor}_tr", 7, nDoor * 2 + 1)
                setSprite("door${nDoor}_bl", 8, nDoor * 2)
                setSprite("door${nDoor}_br", 8, nDoor * 2 + 1)
            }
        }

        val door = BaseEntity(Transform(Vector2f(0f, 0f), 0f, Vector2f(128f, 128f)))
        // Setup keyframes
        val doorComp = MultiSpriteAnimationComponent(door.uid,2, 2).apply { setState("0") }
        for(nDoor in 0 until 14) {
            doorComp.apply {
                addAnimationKeyframe("0", MultiSprite(2, 2)
                    .apply {
                        addSprite(0, 0, atlas.getSprite("door${nDoor}_bl"))
                        addSprite(0, 1, atlas.getSprite("door${nDoor}_br"))
                        addSprite(1, 0, atlas.getSprite("door${nDoor}_tl"))
                        addSprite(1, 1, atlas.getSprite("door${nDoor}_tr"))
                    }, 0.05)
            }
        }

        door.addComponents(doorComp)
        door.addComponent(BackgroundColorComponent(door.uid, 0.75f, 0.75f, 0.75f, 1.0f))

        val mob = BaseEntity(Transform(Vector2f(32f + 16, 16f), 0f, Vector2f(32f, 32f)))
        // Setup keyframes
        val mobComp = SpriteAnimationComponent(mob.uid).apply { setState("0") }
        for(nMob in 0 until 8){
            mobComp.addAnimationKeyframe("0", atlas.getSprite("mob${nMob}"), 0.1)
        }

        mob.addComponent(mobComp)
        mob.addComponent(BackgroundColorComponent(mob.uid, 0.75f, 0.75f, 0.75f, 1.0f))

        engine.addEntity(mob)
        engine.addEntity(door)

    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {}
    override fun onFrame() {}
    override fun onCleanUp() {}

}