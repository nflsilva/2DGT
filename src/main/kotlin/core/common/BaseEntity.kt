package core.common

import core.common.dto.UpdateContext
import core.common.observer.CleanupObserver
import core.common.observer.SignalObserver
import core.common.observer.UpdateObserver
import render.model.Transform
import java.util.*

open class BaseEntity(var transform: Transform) {

    val uid: UUID = UUID.randomUUID()
    private val updateObservers: MutableList<UpdateObserver> = mutableListOf()
    private val signalObservers: MutableList<SignalObserver> = mutableListOf()
    private val cleanupObservers: MutableList<CleanupObserver> = mutableListOf()

    fun onUpdate(context: UpdateContext) {
        for (c in updateObservers) {
            c.onUpdate(context)
        }
    }

    fun onSignal(signal: ComponentSignal) {
        for (s in signalObservers) {
            s.onSignal(signal)
        }
    }

    fun cleanUp() {
        for (c in cleanupObservers) {
            c.onCleanup()
        }
    }

    fun addComponent(component: Component) {
        component.updateObserver?.let { o -> updateObservers.add(o) }
        component.signalObserver?.let { o -> signalObservers.add(o) }
        component.cleanupObserver?.let { o -> cleanupObservers.add(o) }
    }
}