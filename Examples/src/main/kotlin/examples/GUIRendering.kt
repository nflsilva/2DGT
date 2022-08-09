import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.render.ShapeComponent
import core.component.render.SpriteComponent
import core.component.render.TextComponent
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Sprite
import render.dto.Transform
import render.font.DefaultFont
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = GUIRendering(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class BottomMenu(): BaseEntity(Transform(Vector2f(0f, 0f), 0.0f, Vector2f(1280f, 200f ))) {

    private val df = DefaultFont()
    private val titleTextComponent = TextComponent(uid, Vector2f(20f, 160f), "@Hello text rendering!", df, 16f)
    private val statusTextComponent = TextComponent(uid, Vector2f(20f, 140f), "Energy", df, 16f)

    private val bar = BaseEntity(Transform(Vector2f(transform.position).add(120f, 140f), 0.0f, Vector2f(16f)))
    private val horizontalBar: ShapeComponent

    init {

        val background = Sprite("/gui/background.png")
        addComponent(SpriteComponent(uid, background))

        addComponent(titleTextComponent)
        addComponent(statusTextComponent)

        horizontalBar = ShapeComponent(uid, Shape.Type.SQUARE, Color(0.4f, 0.8f, 0.4f, 1.0f), false)
        bar.addComponent(horizontalBar)
    }

    fun addToEngine(engine: CoreEngine){
        engine.addEntity(this)
        engine.addEntity(bar)
    }

    fun setText(text: String){
        titleTextComponent.text = text
    }

    fun getBarWidth(): Float{
        return bar.transform.scale.x
    }

    fun changeBarWidth(width: Float){
        bar.transform.scale.x = width
    }

}

class GUIRendering(private val engine: CoreEngine) : CoreEngineDelegate {

    lateinit var bottomMenu: BottomMenu

    override fun onStart(){
        bottomMenu = BottomMenu()
        bottomMenu.addToEngine(engine)
    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        val t = "Mouse position: ${input.mouseX} x ${input.mouseY}"
        bottomMenu.setText(t)

        val width = bottomMenu.getBarWidth()
        if(input.mouseY < 200 && width < 100){
            bottomMenu.changeBarWidth(width + 0.2f)
        }
        else if(width > 0) {
            bottomMenu.changeBarWidth(width - 0.05f)
        }
    }
    override fun onFrame() {}
    override fun onCleanUp() {}

}