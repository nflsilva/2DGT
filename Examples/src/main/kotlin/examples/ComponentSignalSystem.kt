package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.BaseComponent
import core.component.ComponentSignal
import core.component.ComponentSignalDataField
import core.component.ShapeComponent
import core.dto.UpdateContext
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

class ComponentSignalSystem(private val engine: CoreEngine) : CoreEngineDelegate {

    private val shapes = mutableListOf<Ball>()
    private val gravity: Vector2f = Vector2f(0f, -1000f)

    class Ball(positionX: Float, positionY: Float, radius: Float):
        BaseEntity(Transform(Vector2f(positionX, positionY), 0.0f, Vector2f(radius))) {

        private val acceleration: Vector2f = Vector2f().zero()
        private var oldPosition = Vector2f(positionX, positionY)

        init {
            val rr = Random().nextFloat()
            val rg = Random().nextFloat()
            val rb = Random().nextFloat()
            addComponent(ShapeComponent(Shape.Type.DONUT, Color(rr, rg, rb, 1.0f)))
            addComponent(FloorDetectionComponent())
            addComponent(InvertForceComponent())
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

        fun stop(){
            oldPosition = transform.position
            acceleration.zero()
        }
    }

    class FloorDetectionComponent(): BaseComponent(){

        companion object {
            const val FORCE_DATA_TYPE = "force"
            const val FLOOR_DELECTION_SIGNAL_TYPE = "floor"
        }

        init {
            setUpdateObserver { entity, context -> onUpdate(entity, context) }
        }

        private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
            if(entity.transform.position.y <= 10f){

                context.core.sendSignal(
                    ComponentSignal(entity.uid, FLOOR_DELECTION_SIGNAL_TYPE, entity.uid)
                        .apply {
                            data[FORCE_DATA_TYPE] = ComponentSignalDataField(float = 5000f)
                        })
            }
        }
    }

    class InvertForceComponent(): BaseComponent(){

        var newAcceleration: Float? = null

        init {
            setUpdateObserver { entity, context -> onUpdate(entity, context) }
            setSignalObserver { entity, signal -> onSignal(entity, signal) }
        }

        private fun onUpdate(entity: BaseEntity, context: UpdateContext) {
            val ball = entity as? Ball ?: return
            newAcceleration?.let {
                ball.accelerate(Vector2f(0.0f, it))
                newAcceleration = null
            }

        }

        private fun onSignal(entity: BaseEntity, signal: ComponentSignal) {
            val isSignalType = signal.type == FloorDetectionComponent.FLOOR_DELECTION_SIGNAL_TYPE
            val isForThisEntity = signal.receiverEntityId == entity.uid
            if(!isSignalType || !isForThisEntity) return

            newAcceleration = signal.data[FloorDetectionComponent.FORCE_DATA_TYPE]?.float
        }
    }

    override fun onStart() {

        for(i in 0 until 50) {
            val b0 = Ball(
                Random().nextFloat() * 1280,
                Random().nextFloat() * 720,
                Random().nextFloat() * 25f + 10f)
            engine.addEntity(b0)
            shapes.add(b0)
        }

    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {

        shapes.forEach { shape ->
            shape.accelerate(gravity)
            shape.step(elapsedTime)
        }
    }

    override fun onFrame() {}
    override fun onCleanUp() {}

}