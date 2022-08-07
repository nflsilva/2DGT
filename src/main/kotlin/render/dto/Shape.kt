package render.dto

data class Shape(
    val type: Int,
    val color: Color
) {

    enum class Type(val value: Int) {
        SQUARE(0),
        CIRCLE(1),
        DONUT(2),
    }

}