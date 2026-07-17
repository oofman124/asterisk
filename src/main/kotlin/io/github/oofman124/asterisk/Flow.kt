package io.github.oofman124.asterisk

class Flow {
    private val path = mutableListOf<String>()
    private val visited = mutableSetOf<String>()

    var root: String? = null
        private set
    var standstill: Boolean = false
        private set
    var completed: Boolean = false
        private set
    var standstillReason: String? = null
        private set

    fun begin(root: String? = null) {
        reset()
        this.root = root
        root?.let { path.add(it) }
    }

    fun enter(step: String): Boolean {
        if (!visited.add(step)) {
            markStandstill("Cycle detected at $step")
            return false
        }
        path.add(step)
        return true
    }

    fun exit() {
        if (path.isNotEmpty()) {
            path.removeAt(path.lastIndex)
        }
    }

    fun markStandstill(reason: String? = null) {
        standstill = true
        standstillReason = reason
    }

    fun markCompleted() {
        completed = true
    }

    fun isStandstill(): Boolean = standstill

    fun path(): List<String> = path.toList()

    fun pathString(): String = path.joinToString(" -> ")

    fun depth(): Int = path.size

    fun reset() {
        path.clear()
        visited.clear()
        root = null
        standstill = false
        completed = false
        standstillReason = null
    }
}
