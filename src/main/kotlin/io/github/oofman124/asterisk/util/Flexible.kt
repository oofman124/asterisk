package io.github.oofman124.asterisk.util

/*
Quick example:
fun handleData(input: Flexible<String>) {
    when (input) {
        is Flexible.Single -> println("Single item: ${input.value}")
        is Flexible.Many -> println("Multiple items: ${input.values.size}")
    }
}
*/

sealed class Flexible<out T> {
    data class Single<T>(val value: T) : Flexible<T>()
    data class Many<T>(val values: List<T>) : Flexible<T>()
}