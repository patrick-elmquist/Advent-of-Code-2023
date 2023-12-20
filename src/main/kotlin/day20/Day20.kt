package day20

import common.day

// answer #1: 731517480
// answer #2:

fun main() {
    day(n = 20) {
        part1 { input ->
            val map = input.lines.map { line -> line.split(" -> ") }
                .associate { (module, destinations) -> module to destinations.split(", ") }

            val n = 1000
            var state = State()
            repeat(n) {
                state = pushButton(map, state)
            }
            state.lows * state.highs
        }
        verify {
            expect result 731517480L
            run test 1 expect 32000000L
            run test 2 expect 11687500L
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private const val low = "low"
private const val high = "high"
private const val on = "on"
private const val off = "off"

private fun pushButton(map: Map<String, List<String>>, state: State): State {
    val nameToModule = map.keys.associateBy {
        when {
            it.startsWith('%') -> it.drop(1)
            it.startsWith('&') -> it.drop(1)
            it == "broadcaster" -> it
            else -> error("")
        }
    }

    val queue = ArrayDeque<Triple<String, String, String>>()
    queue.add(Triple("broadcaster", low, ""))

    val memory = state.memory
    val states = state.states

    var lows = 1L
    var highs = 0L

    fun emit(destinations: List<String>, pulse: String, module: String): List<Triple<String, String, String>> {
        return destinations.map { Triple(it, pulse, module) }.print(module).also {
            if (pulse == low) {
                lows += it.size
            } else {
                highs += it.size
            }
        }
    }
    while (queue.isNotEmpty()) {
        val (name, pulse, source) = queue.removeFirst()

        val module = nameToModule.getOrDefault(name, name)
        val destinations = map.getOrDefault(module, emptyList())

        when {
            module == "output" -> {
//                println("output")
            }

            module == "broadcaster" -> {
                queue += emit(destinations, pulse, module)
            }

            module.startsWith('%') -> {
                if (pulse == low) {
                    when (states.getOrPut(module) { off }) {
                        on -> {
                            states[module] = off
                            queue += emit(destinations, low, module)
                        }

                        off -> {
                            states[module] = on
                            queue += emit(destinations, high, module)
                        }

                        else -> error("")
                    }
                }
            }

            module.startsWith('&') -> {
                val memo = memory.getOrPut(module) {
                    map.filter { (_, dest) -> dest.contains(name) }
                        .keys
                        .associateWith { low }
                        .toMutableMap()
                }
                memo[source] = pulse
                queue += if (memo.all { it.value == high }) {
                    emit(destinations, low, module)
                } else {
                    emit(destinations, high, module)
                }
            }
        }
    }

    println()
    return State(
        lows = state.lows + lows,
        highs = state.highs + highs,
        memory = memory,
        states = states,
    )
}

private data class State(
    val lows: Long = 0L,
    val highs: Long = 0L,
    val memory: MutableMap<String, MutableMap<String, String>> = mutableMapOf(),
    val states: MutableMap<String, String> = mutableMapOf(),
)
private fun List<Triple<String, String, String>>.print(module: String): List<Triple<String, String, String>> {
    forEach { (dest, pulse, _) ->
        println("$module -$pulse-> $dest")
    }
    return this
}