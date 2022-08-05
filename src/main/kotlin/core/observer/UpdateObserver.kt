package core.observer

import core.BaseEntity
import core.dto.UpdateContext

interface UpdateObserver {
    fun onUpdate(entity: BaseEntity, context: UpdateContext)
}