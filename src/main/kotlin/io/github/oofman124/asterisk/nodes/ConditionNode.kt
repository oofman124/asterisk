package io.github.oofman124.asterisk.nodes

import io.github.oofman124.asterisk.ports.SignalPort
import io.github.oofman124.asterisk.ports.SignalPortMode
import io.github.oofman124.asterisk.Context

abstract class ConditionNode(id: String) : ExecutableNode(id) {
    val outTrigger = SignalPort("Out", SignalPortMode.SEND)

    init {
        signalPorts["Out"] = outTrigger
    }

    protected abstract fun evaluateCondition(): Boolean

    final override fun onExecute(context: Context?) {
        if (evaluateCondition()) {
            outTrigger.fire(context)
        }
    }
}