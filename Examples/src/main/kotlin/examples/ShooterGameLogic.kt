package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.EngineConfiguration
import core.component.physics.CircleCollisionBoxComponent
import core.component.physics.VerletIntegrationComponent
import core.component.render.MultiSpriteAnimationComponent
import core.component.render.ShapeComponent
import org.joml.Matrix2f
import org.joml.Random
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import render.model.MultiSprite
import render.model.SpriteAtlas
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

    private val bullets: MutableList<Bullet> = mutableListOf()
    private val asteroids: MutableList<Asteroid> = mutableListOf()
    private lateinit var spaceship: Spaceship
    private lateinit var asteroidAtlas: SpriteAtlas

    override fun onStart() {

        asteroidAtlas = SpriteAtlas(
            "/sprite/asteroids-atlas.png",
            16,
            16).apply {
            setSprite("ship0", 0, 0, Vector2f(2f))
            setSprite("thruster0", 1, 4, Vector2f(2f, 1f))
            setSprite("thruster1", 1, 8, Vector2f(2f, 1f))

            setSprite("explosion0", 8, 4, Vector2f(4f))
            setSprite("explosion1", 8, 8, Vector2f(4f))
            setSprite("explosion2", 8, 12, Vector2f(4f))
            setSprite("explosion3", 12, 0, Vector2f(4f))

            setSprite("asteroid0", 12, 4, Vector2f(4f))
            setSprite("asteroid1", 12, 8, Vector2f(4f))
            setSprite("asteroid2", 12, 12, Vector2f(4f))
        }

        spaceship = Spaceship(10f, asteroidAtlas, this)
        engine.addEntity(spaceship)

        addAsteroid()
        addAsteroid()
        addAsteroid()
        addAsteroid()
        addAsteroid()

    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        updateBullets()
        updateAsteroids()
        updateSpaceship(input)
    }

    override fun onFrame() {}
    override fun onCleanUp() {}

    fun addAsteroid(){
        val a0 = Asteroid(Vector2f(
            Random().nextFloat() * 1280,
            Random().nextFloat() * 720),
            Vector2f(Random().nextFloat() * 2 - 1, Random().nextFloat() * 2 - 1), 1000f, asteroidAtlas)
        asteroids.add(a0)
        engine.addEntity(a0)
    }
    fun addBullet(bullet: Bullet){
        bullets.add(bullet)
        engine.addEntity(bullet)
    }

    private fun updateBullets(){
        for(bullet in bullets){
            bullet.onUpdate()

            if(bullet.isDead) {
                engine.removeEntity(bullet)
            }
        }
        bullets.removeIf { it.isDead }
    }
    private fun updateAsteroids(){
        var newDead = 0
        for(a in asteroids){
            a.onUpdate()

            if(!a.isExploding){
                a.collisonBox.collisions.forEach { collision ->
                    bullets.find { it.uid == collision.entityId }?.let { bullet ->
                        a.setExploding()
                        bullet.setDead()
                    }
                }
            }

            if(a.isDead) {
                engine.removeEntity(a)
                newDead += 1
            }
        }
        asteroids.removeIf { it.isDead }

        for(i in 0 until newDead){
            addAsteroid()
        }
    }
    private fun updateSpaceship(input: InputStateData){
        spaceship.onUpdate(input)
    }

}

class Spaceship(mass: Float,
                sprites: SpriteAtlas,
                private val logic: ShooterGameLogic
    ): BaseEntity(Transform(Vector2f(SCREEN_W / 2f, SCREEN_H / 2f), 0f, Vector2f(64f, 96f))) {

    private val thrusterForce = 5000f
    private val rotationForce = 0.02f
    private val bulletForce = 100000.0f
    private val direction = Vector2f(0f, 1f)
    private val verletComp = VerletIntegrationComponent(uid, mass)
    private val msac: MultiSpriteAnimationComponent
    private var updatesSinceShot = 0

    init {

        msac = MultiSpriteAnimationComponent(uid).apply {
            addAnimationKeyframe("idle", MultiSprite(3, 1).apply {
                addSprite(1, 0, Vector2f(1f, 2f), sprites.getSprite("ship0"))
            }, 0.5)

            addAnimationKeyframe("thrusting", MultiSprite(3, 1).apply {
                addSprite(1, 0, Vector2f(1f, 2f), sprites.getSprite("ship0"))
                addSprite(0, 0, Vector2f(1f, 1f), sprites.getSprite("thruster0"))
            }, 0.25)

            addAnimationKeyframe("thrusting", MultiSprite(3, 1).apply {
                addSprite(1, 0, Vector2f(1f, 2f), sprites.getSprite("ship0"))
                addSprite(0, 0, Vector2f(1f, 1f), sprites.getSprite("thruster1"))
            }, 0.25)
        }

        addComponent(msac)
        addComponent(verletComp)
    }

    fun onUpdate(input: InputStateData){
        processInputs(input)
        updatesSinceShot += 1
    }

    private fun processInputs(input: InputStateData){

        var didMove = false
        if(input.isKeyPressed(InputStateData.KEY_W)){
            move(thrusterForce)
            didMove = true
        }
        if(input.isKeyPressed(InputStateData.KEY_S)){
            move(-thrusterForce)
            didMove = true
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

        if(!didMove){
            msac.setState("idle")
        }
    }

    private fun shoot(){
        if(updatesSinceShot >= 10){
            logic.addBullet(Bullet(Vector2f(transform.position.x, transform.position.y), Vector2f(direction), bulletForce))
            updatesSinceShot = 0
        }

    }

    private fun move(force: Float){
        verletComp.applyForce(Vector2f(direction).mul(force))
        msac.setState("thrusting")
    }

    private fun rotate(angle: Float){
        val rm = Matrix2f().rotation(-angle)
        direction.mul(rm).normalize()
        transform.rotation += angle
    }

}

class Bullet(position: Vector2f,
             direction: Vector2f,
             speed: Float
    ): BaseEntity(Transform(Vector2f(position.x, position.y), 0f, Vector2f(4f))) {

    var isDead = false
    private val mov = VerletIntegrationComponent(uid, 1f)
    val collisonBox: CircleCollisionBoxComponent

    init {
        addComponent(ShapeComponent(uid, Shape.Type.CIRCLE, Color(1f, 1f, 1f, 1.0f)))
        collisonBox = CircleCollisionBoxComponent(uid)
        addComponent(collisonBox)
        addComponent(mov)
        mov.applyForce(Vector2f(direction).mul(speed))
    }

    fun onUpdate(){
        if(isDead) return

        isDead = transform.position.x > SCREEN_W || transform.position.x < 0 ||
                transform.position.y > SCREEN_H || transform.position.y < 0
    }
    fun setDead(){
        isDead = true
    }
}

class Asteroid(position: Vector2f,
               direction: Vector2f,
               speed: Float,
               sprites: SpriteAtlas,
): BaseEntity(Transform(Vector2f(position.x, position.y), Random().nextFloat(), Vector2f(64f))) {

    var isDead = false
    var isExploding = false
    private val mov = VerletIntegrationComponent(uid, 1f)
    private val animComp: MultiSpriteAnimationComponent
    val collisonBox: CircleCollisionBoxComponent

    init {
        animComp = MultiSpriteAnimationComponent(uid, true).apply {

            addAnimationKeyframe("normal", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid0"))
            }, 0.25)

            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid0"))
                //addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion3"))
            }, 0.05)

            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid1"))
            }, 0.05)

            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid2"))
            }, 0.05)

            setState("normal")
        }
        addComponent(animComp)
        collisonBox = CircleCollisionBoxComponent(uid)
        addComponent(collisonBox)
        addComponent(mov)
        mov.applyForce(Vector2f(direction).mul(speed))
    }

    fun onUpdate(){
        if(isExploding && animComp.completedState){
            animComp.setState("dead")
            isDead = true
        }
    }

    fun setExploding(){
        isExploding = true
        animComp.setState("exploding")
    }

}