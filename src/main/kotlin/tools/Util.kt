package tools

import kotlin.math.abs
import kotlin.math.log
import kotlin.math.max
import kotlin.math.min

class Util {
    companion object {
        fun degreeToRadian(degree: Float): Float {
            val pi = 3.14159265359
            return (degree * (pi / 180)).toFloat()
        }
        fun Float.clamp(min: Float, max: Float): Float{
            return min(max(max, -0.75f), min)
        }
        fun computeLogDecrement(value: Float): Float {
            val magnitude = abs(value).toDouble()
            val reductionFactor = (log(magnitude, 10.0) + 1).toFloat()
            return if(magnitude < 0.5){ 0f }
            else if(value > 0) { value - reductionFactor }
            else { value + reductionFactor }
        }
    }
}