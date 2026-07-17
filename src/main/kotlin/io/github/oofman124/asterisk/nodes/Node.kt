package io.github.oofman124.asterisk.nodes

import io.github.oofman124.asterisk.ports.SignalPort
import io.github.oofman124.asterisk.ports.ValuePort

abstract class Node(val id: String) {
    val signalPorts = mutableMapOf<String, SignalPort>()
    val valuePorts = mutableMapOf<String, ValuePort<*>>()
}