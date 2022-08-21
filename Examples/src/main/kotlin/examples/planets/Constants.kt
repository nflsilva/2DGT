package examples.planets

class Constants(private val ratio: Double) {

    companion object {
        private const val earthGravity = -9.780                             // 9.780            m/s²
        private const val gravitationalConstant = 6.67430e-11               // 6.67430  x10⁻¹¹  Nm²/kg²
        private const val massSun = 1.989e30                                // 1.98900  x10³⁰   kg
        private const val massEarth = 5.972e24                              // 5.97200  x10²⁴   kg
        private const val massMars = 6.417e23                               // 6.41700  x10²³   kg
        private const val massMoon = 7.342e22                               // 7.34200  x10²²   kg

    }

    val earthGravity: Double
        get() = Companion.earthGravity * ratio

    val gravitationalConstant: Double
        get() = Companion.gravitationalConstant * ratio

    val massSun: Double
        get() = Companion.massSun * ratio

    val massEarth: Double
        get() = Companion.massEarth * ratio

    val massMars: Double
        get() = Companion.massMars * ratio

    val massMoon: Double
        get() = Companion.massMoon * ratio

}