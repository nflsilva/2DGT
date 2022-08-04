package render.dto

import render.model.Color

data class Particle(
    val type: Int,
    val size: Float,
    val color: Color
)