package core.component

import core.BaseEntity
import core.dto.UpdateContext
import core.observer.CleanupObserver
import core.observer.SignalObserver
import core.observer.UpdateObserver
import java.util.*

abstract class BaseComponent {

    protected val uid: UUID = UUID.randomUUID()
    var updateObserver: UpdateObserver? = null
    var signalObserver: SignalObserver? = null
    var cleanupObserver: CleanupObserver? = null

    protected fun setUpdateObserver(observerMethod: (entity: BaseEntity, context: UpdateContext) -> Unit) {
        updateObserver = object : UpdateObserver {
            override fun onUpdate(entity: BaseEntity, context: UpdateContext) {
                observerMethod(entity, context)
            }
        }
    }

    protected fun setSignalObserver(observerMethod: (entity: BaseEntity, signal: ComponentSignal) -> Unit) {
        signalObserver = object : SignalObserver {
            override fun onSignal(entity: BaseEntity, signal: ComponentSignal) {
                observerMethod(entity, signal)
            }
        }
    }

    protected fun setCleanupObserver(observerMethod: (entity: BaseEntity) -> Unit) {
        cleanupObserver = object : CleanupObserver {
            override fun onCleanup(entity: BaseEntity) {
                observerMethod(entity)
            }
        }
    }

}