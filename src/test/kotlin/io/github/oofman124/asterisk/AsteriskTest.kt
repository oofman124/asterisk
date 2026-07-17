package io.github.oofman124.asterisk

import io.github.oofman124.asterisk.nodes.ExecutableNode
import io.github.oofman124.asterisk.nodes.GlobalEventNode
import io.github.oofman124.asterisk.nodes.Node
import io.github.oofman124.asterisk.ports.ValuePort
import io.github.oofman124.asterisk.util.Flexible
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AsteriskTest {

    class LiteralValueNode(id: String, value: Any?) : Node(id) {
        val outValue = ValuePort<Any?>("source_out")

        init {
            valuePorts["Out"] = outValue
            outValue.other = Flexible.Single(value)
        }
    }

    class RelayValueNode(id: String, portId: String) : Node(id) {
        val input = ValuePort<Any?>(portId)

        init {
            valuePorts["In"] = input
        }
    }

    class CaptureNode(
        id: String,
        private val resolvedValue: ValuePort<Any?>,
        private val deadEndValue: ValuePort<Any?>
    ) : ExecutableNode(id) {
        override fun onExecute(context: Context?) {
            val flow = context?.flow ?: return

            context.set("resolved", resolvedValue.getValue(context) ?: -1)
            context.set("dead_end", deadEndValue.getValue(context) == null)
            context.set("flow_root", flow.root ?: "")
            context.set("flow_completed", flow.completed)
            context.set("flow_standstill", flow.standstill)
            context.set("flow_reason", flow.standstillReason ?: "")
        }
    }

    @Test
    fun flowTracksItsOwnState() {
        val flow = Flow()

        flow.begin("GlobalEventNode:root")
        assertEquals("GlobalEventNode:root", flow.root)
        assertEquals("GlobalEventNode:root", flow.pathString())
        assertTrue(flow.enter("ValuePort:relay"))
        assertTrue(flow.enter("ValuePort:source"))
        assertEquals(3, flow.depth())
        assertEquals("GlobalEventNode:root -> ValuePort:relay -> ValuePort:source", flow.pathString())

        flow.exit()
        flow.exit()
        flow.markCompleted()

        assertEquals("GlobalEventNode:root", flow.pathString())
        assertTrue(flow.completed)
        assertFalse(flow.standstill)
    }

    @Test
    fun graphExecutionUsesFlowSignalAndValuePortsTogether() {
        val graph = Graph("integration")

        val sourceNode = LiteralValueNode("source", 42)
        val relayNode = RelayValueNode("relay", "relay_in")
        val deadEndNode = RelayValueNode("dead_end", "dead_end_in")
        val captureNode = CaptureNode("capture", relayNode.input, deadEndNode.input)

        graph.addNode(sourceNode)
        graph.addNode(relayNode)
        graph.addNode(deadEndNode)
        graph.addNode(captureNode)
        graph.addGlobalEventNode("root", ContextTemplate(mapOf("seed" to 7)), timeout = 3)

        graph.connectValuePorts("source", "Out", "relay", "In")
        graph.connectSignals("root", "Out", "capture", "In")

        graph.fireGlobalEventNode("root")

        val rootNode = graph.getNode("root") as GlobalEventNode
        val context = rootNode.context

        assertEquals(7, context.get("seed"))
        assertEquals(42, context.get("resolved"))
        assertEquals(true, context.get("dead_end"))
        assertEquals("GlobalEventNode:root", context.flow.root)
        assertTrue(context.flow.completed)
        assertTrue(context.flow.standstill)
        assertNotNull(context.flow.standstillReason)
        assertTrue(context.flow.standstillReason!!.contains("dead_end_in"))
        assertEquals("GlobalEventNode:root", graph.globalEventFlowPathString("root"))
        assertEquals(true, graph.globalEventFlowIsCompleted("root"))
        assertEquals(true, graph.globalEventFlowIsStandstill("root"))
    }
}
