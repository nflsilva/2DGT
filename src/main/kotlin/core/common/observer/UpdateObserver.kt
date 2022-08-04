package core.common.observer

import core.common.dto.UpdateContext

interface UpdateObserver {
    fun onUpdate(context: UpdateContext)
}