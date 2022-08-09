package render.dto

data class Color(var r: Float, var g: Float, var b: Float, var a: Float) {
    constructor(all: Float) : this(all, all, all, all)

}
