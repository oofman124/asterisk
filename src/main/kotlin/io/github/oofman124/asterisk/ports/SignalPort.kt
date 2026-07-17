package io.github.oofman124.asterisk.ports

import io.github.oofman124.asterisk.Context
import io.github.oofman124.asterisk.util.Flexible

class SignalPort(
    val id: String,
    val mode: SignalPortMode,
    private val event: (context: Context?) -> Unit = {}
) : Port {
    var other: Flexible<SignalPort>? = null

    fun fire(context: Context? = null) {
        if (mode == SignalPortMode.SEND) {
            when (val connection = other) {
                is Flexible.Single -> connection.value.receive(context)
                is Flexible.Many -> connection.values.forEach { it.receive(context) }
                null -> receive(context)
            }
            context?.increment("TotalSignalPropagations", 1)
        }
    }

    fun connect(additions: Flexible<SignalPort>) {
        val current = connections()
        val updated = current + additions.toList()
        other = updated.toFlexible()

    }

    fun disconnect(removals: Flexible<SignalPort>) {
        val removalsSet = removals.toList().toSet()
        val updated = connections().filterNot { it in removalsSet }
        other = updated.toFlexible()
    }

    fun disconnectById(removals: Flexible<String>) {
        val removalIds = removals.toList().toSet()
        val updated = connections().filterNot { it.id in removalIds }
        other = updated.toFlexible()
    }

    fun disconnectAll() {
        other = null
    }

    fun receive(context: Context? = null) {
        event(context)
    }

    private fun connections(): List<SignalPort> {
        return when (val connection = other) {
            is Flexible.Single -> listOf(connection.value)
            is Flexible.Many -> connection.values
            null -> emptyList()
        }
    }

    private fun <T> Flexible<T>.toList(): List<T> {
        return when (this) {
            is Flexible.Single -> listOf(value)
            is Flexible.Many -> values
        }
    }

    private fun List<SignalPort>.toFlexible(): Flexible<SignalPort>? {
        return when (size) {
            0 -> null
            1 -> Flexible.Single(first())
            else -> Flexible.Many(this)
        }
    }
}
