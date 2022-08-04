package core


import core.common.BaseEntity
import core.common.dto.UpdateContext
import render.RenderEngine
import tools.common.Log
import ui.UIEngine
import ui.dto.InputStateData

class CoreEngine(configuration: EngineConfiguration? = null) {

    private var isRunning: Boolean = false
    private var gameObjects: MutableList<BaseEntity> = mutableListOf()

    private val uiEngine: UIEngine
    private val renderEngine: RenderEngine
    private val configuration: EngineConfiguration
    var delegate: CoreEngineDelegate? = null

    init {
        this.configuration = configuration ?: EngineConfiguration.default()
        renderEngine = RenderEngine(this.configuration)
        uiEngine = UIEngine(this.configuration)
    }

    private fun run() {

        var frameStart: Double
        var frameEnd: Double
        var frameDelta = 0.0

        var frames = 0
        var ticks = 0

        val tickTime: Double = 1.0 / ticksPerSecondCap
        val frameTime: Double = 1.0 / framePerSecondCap
        val printTime: Double = 1.0 / printsPerSecondCap

        var timeSinceTick = 0.0
        var timeSinceFrame = 0.0
        var timeSincePrint = 0.0

        while (isRunning) {

            frameStart = uiEngine.getTime()

            if (timeSinceTick >= tickTime) {
                onUpdate(tickTime, uiEngine.getInputState())
                ticks++
                timeSinceTick = 0.0
            }

            if (timeSinceFrame >= frameTime) {
                onFrame()
                frames++
                timeSinceFrame = 0.0
            }

            if (timeSincePrint >= printTime) {
                Log.d("FramesPerSecond: $frames\t\tFrameTime: ${frameDelta * 1000}\t\tTicks: $ticks")
                //Log.d("Camera: ${camera.position}")
                ticks = 0
                frames = 0
                timeSincePrint = 0.0
            }
            if (!uiEngine.isRunning()) {
                isRunning = false
            }

            frameEnd = uiEngine.getTime()
            frameDelta = frameEnd - frameStart
            timeSinceTick += frameDelta
            timeSinceFrame += frameDelta
            timeSincePrint += frameDelta

        }

        onCleanUp()
    }

    private fun onFrame() {
        delegate?.onFrame()
        uiEngine.onFrame()
        renderEngine.onFrame()
    }

    private fun onUpdate(elapsedTime: Double, input: InputStateData) {
        delegate?.onUpdate(elapsedTime, input)
        uiEngine.onUpdate()
        renderEngine.onUpdate()
        gameObjects.forEach { o ->
            o.onUpdate(UpdateContext(elapsedTime, input, renderEngine, entity = o))
        }
    }

    private fun onCleanUp() {
        gameObjects.forEach { o ->
            o.cleanUp()
        }
    }

    fun start() {
        if (isRunning) return
        uiEngine.start()
        renderEngine.onStart()
        delegate?.onStart()
        isRunning = true
        run()
    }

    fun addEntity(entity: BaseEntity) {
        gameObjects.add(entity)
    }

    companion object {
        private var printsPerSecondCap: Int = 1
        private var ticksPerSecondCap: Int = 128
        private var framePerSecondCap: Int = 500
    }

}