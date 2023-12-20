package day20

import common.Input
import common.day
import common.util.leastCommonMultiple

// answer #1: 731517480
// answer #2: 244178746156661

private const val low = "low"
private const val high = "high"
private const val on = "on"
private const val off = "off"

fun main() {
    day(n = 20) {
        part1 { input ->
            val map = parseModuleToDestinationsMap(input)
            val nameToModule = nameToModule(map)

            var state = State()
            repeat(1000) {
                state = pushButton(
                    map = map,
                    nameToModule = nameToModule,
                    state = state,
                    start = "broadcaster"
                )
            }
            state.lows * state.highs
        }
        verify {
            expect result 731517480L
            run test 1 expect 32000000L
            run test 2 expect 11687500L
        }

        part2 { input ->
            val map = parseModuleToDestinationsMap(input)
            val loopStartModules = map.getValue("broadcaster")
            val nameToModule = nameToModule(map)

            loopStartModules.map { start ->
                var state = State()
                var counter = 0L
                while (!state.done) {
                    state = pushButton(
                        map = map,
                        nameToModule = nameToModule,
                        state = state,
                        start = start,
                        endCriteria = "kh" to high // Node leading to rx
                    )
                    counter++
                }
                counter
            }.reduce(::leastCommonMultiple)
        }
        verify {
            expect result 244178746156661L
        }
    }
}

private fun parseModuleToDestinationsMap(input: Input) =
    input.lines.map { line -> line.split(" -> ") }
        .associate { (module, destinations) -> module to destinations.split(", ") }

private fun nameToModule(map: Map<String, List<String>>) =
    map.keys.associateBy {
        when {
            it.startsWith('%') -> it.drop(1)
            it.startsWith('&') -> it.drop(1)
            it == "broadcaster" -> it
            else -> error("")
        }
    }

private fun pushButton(
    map: Map<String, List<String>>,
    nameToModule: Map<String, String>,
    state: State,
    start: String,
    endCriteria: Pair<String, String>? = null,
): State {
    val queue = ArrayDeque<Triple<String, String, String>>()
    queue.add(Triple(start, low, ""))

    var lows = 1L
    var highs = 0L

    fun emit(destinations: List<String>, pulse: String, module: String): List<Triple<String, String, String>> =
        destinations.map { Triple(it, pulse, module) }
            .also {
                if (pulse == low) {
                    lows += it.size
                } else {
                    highs += it.size
                }
            }

    while (queue.isNotEmpty()) {
        val (name, pulse, source) = queue.removeFirst()

        val module = nameToModule.getOrDefault(name, name)
        val destinations = map.getOrDefault(module, emptyList())

        if (name == endCriteria?.first && pulse == endCriteria.second) {
            return state.copy(
                lows = state.lows + lows,
                highs = state.highs + highs,
                done = true,
            )
        }
        queue += when {
            module !in map -> emptyList()

            module == "broadcaster" -> {
                emit(destinations, pulse, module)
            }

            module.startsWith('%') -> {
                if (pulse == low) {
                    when (state.states.getOrPut(module) { off }) {
                        on -> {
                            state.states[module] = off
                            emit(destinations, low, module)
                        }

                        off -> {
                            state.states[module] = on
                            emit(destinations, high, module)
                        }

                        else -> error("")
                    }
                } else {
                    emptyList()
                }
            }

            module.startsWith('&') -> {
                val memo = state.memory.getOrPut(module) {
                    map.filter { (_, destinations) -> destinations.contains(name) }
                        .keys
                        .associateWith { low }
                        .toMutableMap()
                }
                memo[source] = pulse
                if (memo.all { it.value == high }) {
                    emit(destinations, low, module)
                } else {
                    emit(destinations, high, module)
                }
            }

            else -> error("")
        }
    }

    return state.copy(
        lows = state.lows + lows,
        highs = state.highs + highs,
    )
}

private data class State(
    val lows: Long = 0L,
    val highs: Long = 0L,
    val memory: MutableMap<String, MutableMap<String, String>> = mutableMapOf(),
    val states: MutableMap<String, String> = mutableMapOf(),
    val done: Boolean = false,
)
