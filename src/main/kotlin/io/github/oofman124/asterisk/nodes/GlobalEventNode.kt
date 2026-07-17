package io.github.oofman124.asterisk.nodes

import io.github.oofman124.asterisk.Context
import io.github.oofman124.asterisk.ContextTemplate
import io.github.oofman124.asterisk.ports.SignalPort
import io.github.oofman124.asterisk.ports.SignalPortMode

// Kinda like those starting nodes in Scratch. Fired from an external force.
class GlobalEventNode(id: String, parentContext: Context?, var template: ContextTemplate? = null, var timeout: Int = 30): Node(id) {
    val outTrigger = SignalPort("Out", SignalPortMode.SEND)
    val context: Context = Context(parentContext)
    var isRunning = false
    var curTimeout = 0
    init {
        signalPorts["Out"] = outTrigger
    }
    private fun ResetContext() {
        context.reset()
        template?.Apply(context)
        context.flow.begin("GlobalEventNode:$id")
    }
    fun setParentContext(parentContext: Context?) {
        context.setBase(parentContext)
    }
    private fun Fire() {
        outTrigger.fire(context)
        context.flow.markCompleted()
    }
    fun Event() {
        if (!isRunning) {
            isRunning = true
            ResetContext()
            Fire()
            isRunning = false
        } else {
            curTimeout++
            if (curTimeout > timeout) {
                curTimeout = 0
                isRunning = false
                context.flow.markStandstill("GlobalEventNode:$id timed out")
            }
        }
    }
}
