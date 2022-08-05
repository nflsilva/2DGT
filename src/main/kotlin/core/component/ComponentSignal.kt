package core.component

import java.util.*

data class ComponentSignal(
    val senderEntityId: UUID,
    val type: String,
    val receiverEntityId: UUID? = null,
    val data: MutableMap<String, ComponentSignalDataField> = mutableMapOf())