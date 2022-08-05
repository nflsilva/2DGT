package core.dto

import core.BaseEntity
import core.CoreEngine
import render.RenderEngine
import ui.dto.InputStateData

data class UpdateContext(
    val elapsedTime: Double,
    val input: InputStateData,
    val core: CoreEngine,
    val graphics: RenderEngine
)