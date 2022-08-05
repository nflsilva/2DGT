package core.observer

import core.BaseEntity
import core.component.ComponentSignal

interface SignalObserver {
    fun onSignal(entity: BaseEntity, signal: ComponentSignal)
}