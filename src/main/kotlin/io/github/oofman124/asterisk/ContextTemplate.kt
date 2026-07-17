package io.github.oofman124.asterisk

// larping
class ContextTemplate(val template: Map<String, Any>) {
    fun Apply(context: Context) {
        template.forEach { (key, value) ->
            if (context.get(key) == null) {
                context.set(key, value)
            }
        }
    }
}