package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.EngineConfiguration
import core.component.render.ShapeComponent
import core.component.render.SpriteComponent
import org.joml.Matrix2f
import org.joml.Random
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Sprite
import render.dto.Transform
import ui.dto.InputStateData

const val SCREEN_W = 1280
const val SCREEN_H = 720

fun main(args: Array<String>) {

    val configuration = EngineConfiguration.default().apply {
        resolutionWidth = SCREEN_W
        resolutionHeight = SCREEN_H
    }
    val engine = CoreEngine()
    val gameLogic = ShooterGameLogic(engine)
    engine.delegate = gameLogic
    engine.start()

    println("Done!")
}

class ShooterGameLogic(private val engine: CoreEngine) : CoreEngineDelegate {

    private lateinit var spaceship: Spaceship
    private val bullets: MutableList<Bullet> = mutableListOf()

    override fun onStart() {
        spaceship = Spaceship(10f, this)
        engine.addEntity(spaceship)
    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        for(bullet in bullets){
            bullet.onUpdate(elapsedTime)

            if(bullet.isDead) {
                //engine.removeEntity(bullet)
            }
        }
        //bullets.removeIf { it.isDead }

        spaceship.onUpdate(input)
    }

    override fun onFrame() {}
    override fun onCleanUp() {}

    fun addBullet(bullet: Bullet){
        bullets.add(bullet)
        engine.addEntity(bullet)
    }

}

class Spaceship(mass: Float,
                private val logic: ShooterGameLogic
    ): BaseEntity(Transform(Vector2f(SCREEN_W / 2f, SCREEN_H / 2f), 0f, Vector2f(64f))) {

    private val thrusterForce = 5000f
    private val rotationForce = 0.02f
    private val bulletForce = 100000.0f
    private val direction = Vector2f(1f, 0f)
    private val verletComp = VerletIntegrationComponent(uid, mass)
    private var updatesSinceShot = 0

    init {
        addComponent(SpriteComponent(uid, Sprite("/sprite/spaceship.png")))
        addComponent(verletComp)
    }

    fun onUpdate(input: InputStateData){
        processInputs(input)
        updatesSinceShot += 1
    }

    private fun processInputs(input: InputStateData){

        if(input.isKeyPressed(InputStateData.KEY_W)){
            move(thrusterForce)
        }
        if(input.isKeyPressed(InputStateData.KEY_S)){
            move(-thrusterForce)
        }

        if(input.isKeyPressed(InputStateData.KEY_A)){
            rotate(rotationForce)
        }
        if(input.isKeyPressed(InputStateData.KEY_D)){
            rotate(-rotationForce)
        }

        if(input.isKeyPressed(InputStateData.KEY_SPACE)){
            shoot()
        }
    }

    private fun shoot(){
        if(updatesSinceShot >= 10){
            logic.addBullet(Bullet(Vector2f(transform.position.x + transform.scale.x * 0.5f, transform.position.y + transform.scale.y * 0.5f), Vector2f(direction), bulletForce))
            updatesSinceShot = 0
        }

    }

    private fun move(force: Float){
        verletComp.applyForce(Vector2f(direction).mul(force))
    }

    private fun rotate(angle: Float){
        val rm = Matrix2f().rotation(-angle)
        direction.mul(rm).normalize()
        transform.rotation += angle
    }

}

class Bullet(position: Vector2f,
             private val direction: Vector2f,
             private val speed: Float
    ): BaseEntity(Transform(Vector2f(position.x, position.y), 0f, Vector2f(4f))) {

    var isDead = false
    private val mov = VerletIntegrationComponent(uid, 1f)

    init {
        addComponent(ShapeComponent(uid, Shape.Type.CIRCLE, Color(1f, 1f, 1f, 1.0f)))
        addComponent(mov)
        mov.applyForce(Vector2f(direction).mul(speed))
    }


    fun onUpdate(elapsedTime: Double){

        isDead = transform.position.x > SCREEN_W || transform.position.x < 0 ||
                transform.position.y > SCREEN_H || transform.position.y < 0
    }

}




