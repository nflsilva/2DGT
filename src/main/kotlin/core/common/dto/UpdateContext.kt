package core.common.dto

import core.common.BaseEntity
import render.RenderEngine
import ui.dto.InputStateData

data class UpdateContext(
    val elapsedTime: Double,
    val input: InputStateData,
    val graphics: RenderEngine,
    val entity: BaseEntity? = null,
)