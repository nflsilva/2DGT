package core.observer

import core.BaseEntity

interface CleanupObserver {
    fun onCleanup(entity: BaseEntity)
}