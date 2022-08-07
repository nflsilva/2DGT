package render.dto

data class Color(val r: Float, val g: Float, val b: Float, val a: Float) {
    constructor(all: Float) : this(all, all, all, all)
}
