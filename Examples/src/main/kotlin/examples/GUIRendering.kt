import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.TextComponent
import org.joml.Vector2f
import render.dto.Transform
import render.font.DefaultFont
import render.model.BitmapFont
import render.shader.BaseShader
import render.shader.ShaderUniforms
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = GUIRendering(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class GUIRendering(private val engine: CoreEngine) : CoreEngineDelegate {


    override fun onStart() {

        val df = DefaultFont()
        val c = TextComponent(Vector2f().zero(), "@Hello text rendering!", df)

        val e = BaseEntity(Transform(Vector2f(0f, 0f), 0.0f, Vector2f(1f)))
        e.addComponent(c)

        engine.addEntity(e)
    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {}
    override fun onFrame() {}
    override fun onCleanUp() {}

}