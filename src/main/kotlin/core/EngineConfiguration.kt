package core

class EngineConfiguration(var windowTitle: String,
                          var resolutionWidth: Int,
                          var resolutionHeight: Int,
                          var enableVsync: Boolean) {

    companion object {
        fun default(): EngineConfiguration {
            return EngineConfiguration(
                "2DGT",
                1280,
                720,
                true)
        }
    }
}