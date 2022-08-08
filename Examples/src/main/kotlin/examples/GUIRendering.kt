import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.ShapeComponent
import core.component.SpriteComponent
import core.component.TextComponent
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

    private val title = BaseEntity(Transform(Vector2f(transform.position).add(20f, 160f), 0.0f, Vector2f(1f)))
    private val titleTextComponent: TextComponent

    private val status = BaseEntity(Transform(Vector2f(transform.position).add(20f, 140f), 0.0f, Vector2f(1f)))
    private val statusTextComponent: TextComponent

    private val bar = BaseEntity(Transform(Vector2f(transform.position).add(120f, 140f), 0.0f, Vector2f(16f)))
    private val horizontalBar: ShapeComponent

    init {

        val background = Sprite("/gui/background.png")
        addComponent(SpriteComponent(background))

        val df = DefaultFont()
        titleTextComponent = TextComponent(Vector2f().zero(), "@Hello text rendering!", df, 16f)
        title.addComponent(titleTextComponent)

        statusTextComponent = TextComponent(Vector2f().zero(), "Energy", df, 16f)
        status.addComponent(statusTextComponent)

        horizontalBar = ShapeComponent(Shape.Type.SQUARE, Color(0.4f, 0.8f, 0.4f, 1.0f), false)
        bar.addComponent(horizontalBar)
    }

    fun addToEngine(engine: CoreEngine){
        engine.addEntity(this)
        engine.addEntity(title)
        engine.addEntity(status)
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
        if(input.mouseY > 500 && width < 100){
            bottomMenu.changeBarWidth(width + 0.2f)
        }
        else if(width > 0) {
            bottomMenu.changeBarWidth(width - 0.05f)
        }
    }
    override fun onFrame() {}
    override fun onCleanUp() {}

}