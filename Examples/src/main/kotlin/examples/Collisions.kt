package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.component.physics.CircleCollisionBoxComponent
import core.component.render.BackgroundColorComponent
import core.component.render.ShapeComponent
import core.component.render.TextComponent
import org.joml.Math.abs
import org.joml.Random
import org.joml.Vector2f
import physics.dto.CollisionContext
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import render.font.DefaultFont
import ui.dto.InputStateData

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val gameLogic = Collisions(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class Collisions(private val engine: CoreEngine) : CoreEngineDelegate {

    private val shapes = mutableListOf<Particle>()

    class Particle(positionX: Float, positionY: Float, radius: Float):
        BaseEntity(Transform(Vector2f(positionX, positionY), 0.0f, Vector2f(radius))) {

        private val acceleration: Vector2f = Vector2f().zero()
        var oldPosition = Vector2f(positionX, positionY)
        var energy = Random().nextFloat() * 100f

        private val shape = Shape(Shape.Type.CIRCLE.value, Color(1f))
        private val collisionComponent = CircleCollisionBoxComponent(uid)
        private val font = DefaultFont()
        private val textComponent = TextComponent(uid, Vector2f(transform.scale).div(3f), "ABCDE", font, 16f)

        init {
            addComponent(ShapeComponent(uid, shape))
            addComponent(collisionComponent)
            addComponent(textComponent)
            addComponent(BackgroundColorComponent(uid, Color(0.25f)))
        }

        fun step(dt: Double){

            textComponent.text = "${energy.toInt()} %"

            shape.color.r = energy / 100f
            shape.color.g = energy / 100f
            shape.color.b = energy / 100f

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

        fun getCollisions(): List<CollisionContext>{
            return collisionComponent.collisions
        }

    }

    override fun onStart() {

        val rx = 1280f / 2
        val e0 = Particle(rx, Random().nextFloat() * 720, 100f)
        val e1 = Particle(rx, Random().nextFloat() * 720, 100f)
        engine.addEntity(e0)
        engine.addEntity(e1)
        shapes.add(e0)
        shapes.add(e1)
    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {

        processInputs(input)

        shapes.forEach {
            it.step(elapsedTime)

            processCollisions(it)
        }
    }

    private fun processInputs(input: InputStateData){
        val forceMagnitude = 500f
        val force = Vector2f().zero()
        if(input.isKeyPressed(InputStateData.KEY_W)){
            force.add(Vector2f(0f, 1f))
        }
        if(input.isKeyPressed(InputStateData.KEY_S)){
            force.add(Vector2f(0f, -1f))
        }
        if(input.isKeyPressed(InputStateData.KEY_A)){
            force.add(Vector2f(-1f, 0f))
        }
        if(input.isKeyPressed(InputStateData.KEY_D)){
            force.add(Vector2f(1f, 0f))
        }

        val usedEnergy = (abs(force.x) + abs(force.y)) / 20f
        shapes[0].accelerate(force.mul(forceMagnitude))
        shapes[0].energy -= usedEnergy
    }

    private fun processCollisions(particle: Particle){

        val transferAmount = 0.5f
        particle.getCollisions().forEach { otherContext ->
            val otherParticle = shapes.find { it.uid == otherContext.entityId } ?: return

            if(particle.energy > otherParticle.energy){
                particle.energy -= transferAmount
                otherParticle.energy += transferAmount
            }
            else if(particle.energy < otherParticle.energy){
                particle.energy += transferAmount
                otherParticle.energy -= transferAmount
            }

            val diff =
                particle.transform.scale.x / 2f + otherParticle.transform.scale.x / 2f - otherContext.distance

            val collisionDirection = Vector2f(particle.transform.position)
                .sub(otherParticle.transform.position).normalize()

            particle.transform.translate(Vector2f(collisionDirection).mul(Vector2f(diff / 2f)))
            otherParticle.transform.translate(Vector2f(collisionDirection).mul(Vector2f(-diff / 2f)))

        }
    }

    override fun onFrame() {}

    override fun onCleanUp() {}

}