package io.github.oofman124.asterisk.nodes

import io.github.oofman124.asterisk.ports.SignalPort
import io.github.oofman124.asterisk.ports.SignalPortMode
import io.github.oofman124.asterisk.Context

abstract class ExecutableNode(id: String) : Node(id) {
    val inTrigger = SignalPort("In", SignalPortMode.RECEIVE) { context ->
        onExecute(context)
    }

    init {
        signalPorts["In"] = inTrigger
    }

    protected abstract fun onExecute(context: Context? = null)
}