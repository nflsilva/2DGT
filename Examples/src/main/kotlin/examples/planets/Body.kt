package examples.planets

import core.BaseEntity
import core.component.physics.VerletIntegrationComponent
import core.component.render.ShapeComponent
import org.joml.Vector2f
import render.dto.Color
import render.dto.Shape
import render.dto.Transform
import java.lang.Float.max

open class Body(positionX: Float, positionY: Float, var mass: Double, radiusRatio: Double) :
    BaseEntity(
        Transform(
            Vector2f(positionX, positionY),
            0.0f,
            Vector2f(max(5f, (mass * radiusRatio).toFloat()))
        )
    ) {

    private val verlet = VerletIntegrationComponent(uid, mass.toFloat())

    init {
        addComponent(ShapeComponent(uid, Shape.Type.CIRCLE, Color(1f, 1f, 1f, 1.0f)))
        addComponent(verlet)
    }

    fun changeMass(mass: Double) {
        verlet.mass = mass.toFloat()
        this.mass = mass
    }

    fun applyForce(force: Vector2f) {
        verlet.applyForce(force)
    }

}