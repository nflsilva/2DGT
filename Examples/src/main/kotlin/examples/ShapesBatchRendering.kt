package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.physics.VerletIntegrationComponent
import core.component.render.ShapeComponent
import org.joml.Random
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = ShapesBatchRendering(engine)
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

        private val mov = VerletIntegrationComponent(uid, 1f)

        init {
            val rr = Random().nextFloat()
            val rg = Random().nextFloat()
            val rb = Random().nextFloat()
            addComponent(ShapeComponent(uid, Shape.Type.DONUT, Color(rr, rg, rb, 1.0f)))
            addComponent(mov)
        }

        fun applyForce(force: Vector2f){
            mov.applyForce(force)
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
            shape.applyForce(gravity)

            if(shape.transform.position.y <= 20f){
                shape.applyForce(Vector2f(0f, 5000f))
            }
        }

        if(shapes.size >= lastPrintOn + 100) {
            println(shapes.size.toString() + " shapes")
            lastPrintOn = shapes.size
        }

    }

    override fun onFrame() {}
    override fun onCleanUp() {}

}