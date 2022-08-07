package core

import core.component.BaseComponent
import core.component.ComponentSignal
import core.dto.UpdateContext
import core.observer.CleanupObserver
import core.observer.SignalObserver
import core.observer.UpdateObserver
import render.dto.Transform
import java.util.*

open class BaseEntity(var transform: Transform) {

    val uid: UUID = UUID.randomUUID()
    private val updateObservers: MutableList<UpdateObserver> = mutableListOf()
    private val signalObservers: MutableList<SignalObserver> = mutableListOf()
    private val cleanupObservers: MutableList<CleanupObserver> = mutableListOf()

    fun onUpdate(context: UpdateContext) {
        for (c in updateObservers) {
            c.onUpdate(this, context)
        }
    }
    fun onSignal(signal: ComponentSignal) {
        for (s in signalObservers) {
            s.onSignal(this, signal)
        }
    }
    fun cleanUp() {
        for (c in cleanupObservers) {
            c.onCleanup(this)
        }
    }

    fun addComponent(component: BaseComponent) {
        component.updateObserver?.let { o -> updateObservers.add(o) }
        component.signalObserver?.let { o -> signalObservers.add(o) }
        component.cleanupObserver?.let { o -> cleanupObservers.add(o) }
    }

    fun addComponents(vararg components: BaseComponent) {
        components.forEach { c ->
            addComponent(c)
        }
    }
}