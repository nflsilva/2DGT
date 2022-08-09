import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.render.ShapeComponent
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = ScreenOrientation(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class ScreenOrientation(private val engine: CoreEngine) : CoreEngineDelegate {

    override fun onStart() {

        val bottomLeft = BaseEntity(Transform(
            Vector2f(0f, 0f),
            0.0f,
            Vector2f(64f, 64f)))
        bottomLeft.addComponent(ShapeComponent(bottomLeft.uid, Shape.Type.CIRCLE, Color(1.0f, 0.0f, 0.0f, 1.0f)))

        val middle = BaseEntity(Transform(
            Vector2f(1280f/2, 720f/2),
            0.0f,
            Vector2f(64f, 64f)))
        middle.addComponent(ShapeComponent(middle.uid, Shape.Type.CIRCLE, Color(0.0f, 1.0f, 0.0f, 1.0f)))

        engine.addEntity(bottomLeft)
        engine.addEntity(middle)

    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {}
    override fun onFrame() {}
    override fun onCleanUp() {}

}