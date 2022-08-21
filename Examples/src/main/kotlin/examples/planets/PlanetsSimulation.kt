package examples.planets

import core.CoreEngine
import core.CoreEngineDelegate
import org.joml.Math.abs
import org.joml.Vector2f
import ui.dto.InputStateData
import kotlin.math.sqrt

fun main(args: Array<String>) {

    val engine = CoreEngine()
    val simulation = PlanetsSimulation(engine)
    engine.delegate = simulation
    engine.start()

    println("Done!")

}

class PlanetsSimulation(private val engine: CoreEngine) : CoreEngineDelegate {

    companion object {
        const val CALC_RATIO = 1e-7
        const val DRAW_RATIO = 1e-22
    }

    private val cx = 1280f / 2
    private val cy = 720f / 2

    private val constants = Constants(CALC_RATIO)
    private val bodies = mutableListOf<Body>()
    private lateinit var star: Body

    override fun onStart() {

        addSolarSystem(cx, cy)

    }

    override fun onUpdate(elapsedTime: Double, input: InputStateData) {
        computeForces()
    }

    private fun addSolarSystem(sx: Float, sy: Float){

        var e = Body(sx, sy + 55f, constants.massEarth, DRAW_RATIO)
        e.applyForce(Vector2f(1f, 0f).mul(1e22.toFloat()))
        engine.addEntity(e)
        bodies.add(e)

        e = Body(sx, sy - 50f, constants.massEarth, DRAW_RATIO)
        e.applyForce(Vector2f(1f, 0f).mul(1e22.toFloat()))
        engine.addEntity(e)
        bodies.add(e)

        e = Body(sx, sy + 100f, constants.massEarth, DRAW_RATIO)
        e.applyForce(Vector2f(1f, 0f).mul(1e22.toFloat()))
        engine.addEntity(e)
        bodies.add(e)

        e = Body(sx, sy + 115f, constants.massMars, DRAW_RATIO)
        e.applyForce(Vector2f(1f, 0f).mul(1e21.toFloat()))
        engine.addEntity(e)
        bodies.add(e)

        e = Body(sx, sy + 135f, constants.massMars, DRAW_RATIO)
        e.applyForce(Vector2f(1f, 0f).mul(1e21.toFloat()))
        engine.addEntity(e)
        bodies.add(e)

        star = Body(sx, sy, constants.massSun, DRAW_RATIO)
        engine.addEntity(star)
        bodies.add(star)
    }

    private fun computeForces() {

        val bodiesToFuse = mutableListOf<Pair<Body, Body>>()
        for (b0i in 0 until bodies.size - 1) {

            val b0 = bodies[b0i]

            for (b1i in b0i + 1 until bodies.size) {
                val b1 = bodies[b1i]

                // b1 -> b0
                val dv = Vector2f(b0.transform.position)
                    .sub(Vector2f(b1.transform.position))

                val r = abs(sqrt(dv.x * dv.x + dv.y * dv.y))
                if(r <= 1){
                    bodiesToFuse.add(Pair(b0, b1))
                }

                // F = G * (m1 m2) / r²
                val force = constants.gravitationalConstant * ((b0.mass * b1.mass) / (r * r))

                dv.normalize()
                b0.applyForce(dv.mul(-force.toFloat()))
                b1.applyForce(dv.mul(force.toFloat()))

                //println("b1 -> b0 $force N")
            }
        }

        for(p in bodiesToFuse){
            p.first.mass = p.first.mass + p.second.mass
            bodies.remove(p.second)
            engine.removeEntity(p.second)
        }

        pullStarToCenter()
    }

    private fun pullStarToCenter(){
        val center = Vector2f(cx, cy)
        val dv = center.sub(Vector2f(star.transform.position))
        val distanceToCenter = abs(sqrt(dv.x * dv.x + dv.y * dv.y))
        if(distanceToCenter == 0f) return

        star.applyForce(dv.normalize().mul(distanceToCenter * 1E23.toFloat()))

    }

    override fun onFrame() {}
    override fun onCleanUp() {}

}