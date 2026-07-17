// io/github/oofman124/asterisk/graph/Graph.kt
package io.github.oofman124.asterisk

import io.github.oofman124.asterisk.nodes.Node
import io.github.oofman124.asterisk.nodes.GlobalEventNode
import io.github.oofman124.asterisk.util.Flexible

class Graph(val name: String) {
    // The shared memory specific to this individual graph execution loop

    // Registry of all node instances living inside this graph container
    val nodes = mutableMapOf<String, Node>()
    val globalContext = Context()

    /**
     * Registers a new node into the graph container.
     */

    fun applyContextTemplate(template: ContextTemplate) {
        template.Apply(globalContext)
    }

    fun addGlobalEventNode(id: String, template: ContextTemplate?, timeout: Int = 30) {
        nodes[id] = GlobalEventNode(id, globalContext, template, timeout)
    }
    
    fun fireGlobalEventNode(id: String) {
        (nodes[id] as? GlobalEventNode)?.Event()
    }

    fun globalEventFlowPath(id: String): List<String>? {
        return (nodes[id] as? GlobalEventNode)?.context?.flow?.path()
    }

    fun globalEventFlowPathString(id: String): String? {
        return (nodes[id] as? GlobalEventNode)?.context?.flow?.pathString()
    }
    
    fun globalEventFlowIsStandstill(id: String): Boolean? {
        return (nodes[id] as? GlobalEventNode)?.context?.flow?.isStandstill()
    }

    fun globalEventFlowIsCompleted(id: String): Boolean? {
        return (nodes[id] as? GlobalEventNode)?.context?.flow?.completed
    }

    fun addNode(node: Node) {
        nodes[node.id] = node
    }

    /**
     * Safely retrieves a node instance from the container by its ID string.
     */
    fun getNode(id: String): Node? = nodes[id]

    /**
     * Connects two signal ports together inside the graph container safely.
     */
    fun connectSignals(fromNodeId: String, fromPortName: String, toNodeId: String, toPortName: String) {
        val sourceNode = nodes[fromNodeId] ?: return
        val targetNode = nodes[toNodeId] ?: return

        val sourcePort = sourceNode.signalPorts[fromPortName] ?: return
        val targetPort = targetNode.signalPorts[toPortName] ?: return

        sourcePort.connect(Flexible.Single(targetPort))
    }

    /**
     * Connects two value ports together inside the graph container safely.
     */
    fun connectValuePorts(fromNodeId: String, fromPortName: String, toNodeId: String, toPortName: String) {
        val sourceNode = nodes[fromNodeId] ?: return
        val targetNode = nodes[toNodeId] ?: return

        val sourcePort = sourceNode.valuePorts[fromPortName] ?: return
        val targetPort = targetNode.valuePorts[toPortName] ?: return

        targetPort.connect(Flexible.Single(sourcePort))
    }

    /**
     * Wipes the graph container, unlinking nodes and memory.
     */
    fun clear() {
        nodes.clear()
    }
}
