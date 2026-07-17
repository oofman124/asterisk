package io.github.oofman124.asterisk.ports

import io.github.oofman124.asterisk.Context
import io.github.oofman124.asterisk.util.Flexible

class ValuePort<T>(val id: String = "") : Port {
    var other: Flexible<Any?>? = null

    fun connect(additions: Flexible<Any?>) {
        val updated = connections() + additions.toList()
        other = updated.toFlexible()
    }

    fun disconnect(removals: Flexible<Any?>) {
        val removalsSet = removals.toList().toSet()
        val updated = connections().filterNot { it in removalsSet }
        other = updated.toFlexible()
    }

    fun disconnectById(removals: Flexible<String>) {
        val removalIds = removals.toList().toSet()
        val updated = connections().filterNot { it is ValuePort<*> && it.id in removalIds }
        other = updated.toFlexible()
    }

    fun disconnectAll() {
        other = null
    }

    fun getValue(context: Context? = null): Any? {
        val label = flowLabel()
        val entered = context?.flow?.enter(label) != false
        if (!entered) {
            return null
        }

        return try {
            when (val connected = other) {
                is Flexible.Single -> resolveValue(connected.value, context)
                is Flexible.Many -> connected.values.map { resolveValue(it, context) }
                null -> {
                    context?.flow?.markStandstill("Unconnected value port $label")
                    null
                }
            }
        } finally {
            context?.flow?.exit()
        }
    }

    private fun resolveValue(value: Any?, context: Context? = null): Any? {
        return when (value) {
            is ValuePort<*> -> value.getValue(context)
            else -> value.also {
                context?.flow?.markCompleted()
            }
        }
    }

    private fun connections(): List<Any?> {
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

    private fun List<Any?>.toFlexible(): Flexible<Any?>? {
        return when (size) {
            0 -> null
            1 -> Flexible.Single(first())
            else -> Flexible.Many(this)
        }
    }

    private fun flowLabel(): String {
        return if (id.isBlank()) {
            "ValuePort@${System.identityHashCode(this).toString(16)}"
        } else {
            "ValuePort:$id"
        }
    }
}
