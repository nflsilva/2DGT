package render.batch

import render.dto.Particle
import render.dto.Transform

class ParticlesBatch(maxEntities: Int) :
    BaseBatch(maxEntities, 1, 1) {

    companion object {
        const val POSITION_INDEX = 0
        const val SIZE_INDEX = 1
        const val TYPE_INDEX = 2
        const val COLOR_INDEX = 3
    }

    init {
        addFloatAttributeBuffer(POSITION_INDEX, 2)
        addFloatAttributeBuffer(SIZE_INDEX, 1)
        addIntAttributeBuffer(TYPE_INDEX, 1)
        addFloatAttributeBuffer(COLOR_INDEX, 4)
    }

    fun addParticle(particle: Particle, transform: Transform) {
        addAttributeData(POSITION_INDEX, transform.position.x, transform.position.y)
        addAttributeData(SIZE_INDEX, particle.size)
        addAttributeData(TYPE_INDEX, particle.type)
        addAttributeData(COLOR_INDEX, particle.color.r, particle.color.g, particle.color.b, particle.color.a)
        addIndexData(nEntities)
        nEntities += 1
    }

}