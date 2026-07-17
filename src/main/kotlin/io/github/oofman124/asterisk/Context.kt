package io.github.oofman124.asterisk


class Context(var parent: Context? = null) {
    val flow: Flow = Flow()
    val map: MutableMap<String, Any?> = mutableMapOf<String, Any?>()

    public fun set(key: String, value: Any?) {
        map[key] = value
    }
    public fun setBase(newBase: Context?) {
        parent = newBase
    }
    public fun get(key: String): Any? {
        return map[key] ?: parent?.get(key)
    }
    public fun increment(key: String, increment: Number) {
        val current = map[key]
        if (current is Number) {
            map[key] = when (current) {
                is Int -> current + increment.toInt()
                is Long -> current + increment.toLong()
                is Float -> current + increment.toFloat()
                is Double -> current + increment.toDouble()
                is Short -> (current.toInt() + increment.toInt()).toShort()
                is Byte -> (current.toInt() + increment.toInt()).toByte()
                else -> current
            }
        }
    }

    public fun reset() {
        map.clear()
        flow.reset()
    }
}
