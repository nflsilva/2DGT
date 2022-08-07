package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.ShapeComponent
import org.joml.Random
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = ComponentSignalSystem(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class ShapesBatchRendering(private val engine: CoreEngine) : CoreEngineDelegate {

    private val shapes = mutableListOf<Ball>()
    private val gravity: Vector2f = Vector2f(0f, -1000f)
    private var lastPrintOn: Int = 0

    class Ball(positionX: Float, positionY: Float, radius: Float):
        BaseEntity(Transform(Vector2f(positionX, positionY), 0.0f, Vector2f(radius))) {

        private val acceleration: Vector2f = Vector2f().zero()
        var oldPosition = Vector2f(positionX, positionY)

        init {
            val rr = Random().nextFloat()
            val rg = Random().nextFloat()
            val rb = Random().nextFloat()
            addComponent(ShapeComponent(Shape.Type.DONUT, Color(rr, rg, rb, 1.0f)))
        }

        fun step(dt: Double){

            var currentPosition = Vector2f(transform.position.x, transform.position.y)
            val velocity = Vector2f(currentPosition.x, currentPosition.y).sub(oldPosition)

            oldPosition = Vector2f(currentPosition.x, currentPosition.y)

            acceleration.mul(dt.toFloat() * dt.toFloat())
            currentPosition = currentPosition.add(velocity).add(acceleration)

            transform.position.x = currentPosition.x
            transform.position.y = currentPosition.y

            acceleration.zero()
        }

        fun accelerate(value: Vector2f){
            acceleration.add(value)
        }
    }

    override fun onStart() {}

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {

        val rx = Random().nextFloat() * 1280
        val ry = Random().nextFloat() * 720
        val e = Ball(rx, ry, Random().nextFloat() * 25f + 10f)
        engine.addEntity(e)
        shapes.add(e)

        shapes.forEach { shape ->
            shape.accelerate(gravity)

            if(shape.transform.position.y <= 20f){
                shape.accelerate(Vector2f(0f, 5000f))
            }

            shape.step(elapsedTime)
        }

        if(shapes.size >= lastPrintOn + 100) {
            println(shapes.size.toString() + " shapes")
            lastPrintOn = shapes.size
        }

    }

    override fun onFrame() {}
    override fun onCleanUp() {}

}