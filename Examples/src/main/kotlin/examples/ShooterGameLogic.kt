package examples

import core.BaseEntity
import core.CoreEngine
import core.CoreEngineDelegate
import core.EngineConfiguration
import core.component.input.RotateComponent
import core.component.physics.CircleCollisionBoxComponent
import core.component.physics.VerletIntegrationComponent
import core.component.render.MultiSpriteAnimationComponent
import core.component.render.SpriteComponent
import core.component.render.TextComponent
import org.joml.Math.abs
import org.joml.Matrix2f
import org.joml.Random
import org.joml.Vector2f
import render.dto.Transform
import render.font.DefaultFont
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
    private lateinit var hud: HUD
    private var score: Int = 0
    private var maxAsteroids: Int = 6

    override fun onStart() {

        asteroidAtlas = SpriteAtlas(
            "/sprite/asteroids-atlas.png",
            16,
            16).apply {
            setSprite("ship0", 0, 0, Vector2f(2f))
            setSprite("thruster0", 1, 4, Vector2f(2f, 1f))
            setSprite("thruster1", 1, 8, Vector2f(2f, 1f))

            setSprite("bullet0", 3, 6, Vector2f(2f, 1f))

            setSprite("explosion0", 8, 4, Vector2f(4f))
            setSprite("explosion1", 8, 8, Vector2f(4f))
            setSprite("explosion2", 8, 12, Vector2f(4f))
            setSprite("explosion3", 12, 0, Vector2f(4f))

            setSprite("asteroid0", 12, 4, Vector2f(4f))
            setSprite("asteroid1", 12, 8, Vector2f(4f))
            setSprite("asteroid2", 12, 12, Vector2f(4f))
        }

        spaceship = Spaceship(10f, asteroidAtlas, this)
        hud = HUD(Vector2f().zero())

        engine.addEntity(spaceship)
        engine.addEntity(hud)

        for(i in 0 until maxAsteroids){
            addAsteroid()
        }
    }
    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        updateBullets()
        updateAsteroids()
        updateSpaceship(input)
        updateScore()
    }

    override fun onFrame() {}
    override fun onCleanUp() {}

    private fun addAsteroid(){
        val p = Vector2f(Random().nextFloat() * 1280, Random().nextFloat() * 720)

        val a0 = Asteroid(p,
            Vector2f(spaceship.transform.position).sub(p).normalize(),
            10000f,
            asteroidAtlas)
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

                a.collisionBox.collisions.forEach { collision ->

                    bullets.find { it.uid == collision.entityId }?.let { bullet ->
                        a.damage()
                        bullet.setDead()
                    }

                    asteroids.find { it.uid == collision.entityId }?.let { astroid ->

                        val diff =
                            a.transform.scale.x / 2f + astroid.transform.scale.x / 2f - collision.distance

                        val collisionDirection = Vector2f(a.transform.position)
                            .sub(astroid.transform.position).normalize()

                        a.transform.translate(Vector2f(collisionDirection).mul(Vector2f(diff / 2f)))
                        astroid.transform.translate(Vector2f(collisionDirection).mul(Vector2f(-diff / 2f)))

                    }
                }

                if(a.transform.position.x <= 0){
                    a.transform.translate(Vector2f(abs(a.transform.position.x)))
                }
                if(a.transform.position.x >= SCREEN_W){
                    a.transform.translate(Vector2f(-(a.transform.position.x - SCREEN_W)))
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
            score += 1
        }
    }
    private fun updateSpaceship(input: InputStateData){
        spaceship.onUpdate(input)

        if(spaceship.isDead) {
            engine.removeEntity(spaceship)
        }
        else if(!spaceship.isExploding){
            spaceship.collisionBox.collisions.forEach { collision ->
                asteroids.find { it.uid == collision.entityId }?.let { asteroid ->


                    asteroid.explode()
                    spaceship.explode()
                }
            }

            if(spaceship.transform.position.x < 0f){
                spaceship.transform.position.x = 0f
            }
        }
    }
    private fun updateScore(){
        hud.setScore(score)
    }
}

class Spaceship(mass: Float, private val sprites: SpriteAtlas, private val logic: ShooterGameLogic): BaseEntity(Transform(Vector2f(SCREEN_W / 2f, SCREEN_H / 2f), 0f, Vector2f(64f, 96f))) {

    private val thrusterForce = 5000f
    private val rotationForce = 0.05f
    private val bulletForce = 100000.0f
    private val direction = Vector2f(0f, 1f)
    private val verletComp = VerletIntegrationComponent(uid, mass)
    private val msac: MultiSpriteAnimationComponent = MultiSpriteAnimationComponent(uid).apply {
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

        addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
        {
            addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion0"))
        }, 0.02)
        addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
        {
            addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion1"))
        }, 0.02)
        addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
        {
            addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion2"))
        }, 0.02)
        addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
        {
            addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion3"))
        }, 0.02)

    }
    private var updatesSinceShot = 0
    val collisionBox: CircleCollisionBoxComponent
    var isExploding = false
    var isDead = false

    init {
        addComponent(msac)
        addComponent(verletComp)

        collisionBox = CircleCollisionBoxComponent(uid)
        addComponent(collisionBox)
    }

    fun onUpdate(input: InputStateData){
        if(isExploding || isDead){
            if(msac.completedState){
                msac.setState("dead")
                isDead = true
            }
        }
        else {
            processInputs(input)
            updatesSinceShot += 1
        }
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

            logic.addBullet(
                Bullet(
                    Vector2f(transform.position),
                    Vector2f(direction),
                    bulletForce,
                    transform.rotation,
                    sprites))

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

    fun explode(){
        isExploding = true
        msac.setState("exploding")
    }

}

class Bullet(position: Vector2f,
             direction: Vector2f,
             speed: Float,
             rotation: Float,
             sprites: SpriteAtlas
    ): BaseEntity(Transform(Vector2f(position.x, position.y), rotation, Vector2f(16f))) {

    var isDead = false
    private val mov = VerletIntegrationComponent(uid, 1f)
    val collisionBox: CircleCollisionBoxComponent

    init {
        addComponent(SpriteComponent(uid,  sprites.getSprite("bullet0")))

        collisionBox = CircleCollisionBoxComponent(uid)
        addComponent(collisionBox)

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
): BaseEntity(Transform(Vector2f(position.x, position.y), 0f, Vector2f(64f))) {

    var isDead = false
    var isExploding = false
    var health = 1
    private val mov = VerletIntegrationComponent(uid, 1f)
    private val animComp: MultiSpriteAnimationComponent
    val collisionBox: CircleCollisionBoxComponent

    init {

        animComp = MultiSpriteAnimationComponent(uid, true).apply {

            addAnimationKeyframe("normal", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid0"))
            }, 0.25)

            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion0"))
            }, 0.02)
            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion1"))
            }, 0.02)
            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion2"))
            }, 0.02)
            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("explosion3"))
            }, 0.02)
            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid1"))
            }, 0.02)
            addAnimationKeyframe("exploding", MultiSprite(1, 1).apply
            {
                addSprite(0, 0, Vector2f(1f), sprites.getSprite("asteroid2"))
            }, 0.02)

            setState("normal")
        }
        addComponent(animComp)

        collisionBox = CircleCollisionBoxComponent(uid)
        addComponent(collisionBox)

        addComponent(mov)
        mov.applyForce(Vector2f(direction).mul(speed))

        addComponent(RotateComponent(uid, Random().nextFloat() * 5f))
    }

    fun onUpdate(){
        if(isExploding && animComp.completedState){
            animComp.setState("dead")
            isDead = true
        }
    }

    fun damage(){
        health -= 1
        transform.scale.add(Vector2f(2f))
        if(health == 0){
            explode()
        }
    }
    fun explode(){
        isExploding = true
        animComp.setState("exploding")
    }

}

class HUD(position: Vector2f): BaseEntity(Transform(Vector2f(position), 0f, Vector2f(SCREEN_W.toFloat(), 100f))){

    private val font = DefaultFont()
    private val scoreText = TextComponent(uid, Vector2f(10f), "", font, 16f)

    init {
        addComponent(scoreText)

    }

    fun setScore(score: Int){
        scoreText.text = "Score: $score"
    }

}