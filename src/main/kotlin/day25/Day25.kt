package day25

import common.day
import common.util.log

// answer #1: 550080
// answer #2:

fun main() {
    day(n = 25) {
        part1 { input ->
            val map = mutableMapOf<String, MutableSet<String>>().apply {
                input.lines
                    .associate { line -> line.split(": ").let { (v, edges) -> v to edges.split(" ") } }
                    .forEach { (v, edges) ->
                        val eToV = getOrPut(v) { mutableSetOf() }
                        eToV.addAll(edges)
                        edges.forEach { v1 ->
                            getOrPut(v1) { mutableSetOf() }.add(v)
                        }
                    }
            }
            val immutableMap = map.mapValues { it.value.toSet() } log "immutable"
            (map.mapValues { it.value.toList() } log "immutable").mapValues { it.value.size }.forEach {
                it.log()
            }
            val edges = immutableMap.flatMap { (v, e) -> e }.toSet()

            val counter = mutableMapOf<Set<String>, Int>()
            for (start in immutableMap.keys) {
                reaches(immutableMap, start, counter)
            }
            val mostReached = counter.entries.sortedByDescending { it.value }
            for (i in mostReached.indices) {
                for (j in i + 1 ..< mostReached.size) {
                    for (k in j + 1 ..< mostReached.size) {
                        val severedConnections = immutableMap.removeEdges(
                            mostReached[i].key, mostReached[j].key, mostReached[k].key
                        )
                        val size = reaches(severedConnections, start = immutableMap.keys.first(), counter = null)
                        if (size < immutableMap.size) {
                            return@part1 size * (immutableMap.size - size)
                        }
                    }
                }
            }
        }
        verify {
            expect result 550080
            run test 1 expect 54
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun Map<String, Set<String>>.removeEdges(
    e1: Set<String>,
    e2: Set<String>,
    e3: Set<String>,
): Map<String, Set<String>> {
    val mutable = this.mapValues { it.value.toMutableSet() }
    listOf(e1.toList(), e2.toList(), e3.toList()).forEach { (start, end) ->
        mutable.getValue(start).remove(end)
        mutable.getValue(end).remove(start)
    }
    return mutable
}

private fun reaches(
    graph: Map<String, Set<String>>,
    start: String,
    counter: MutableMap<Set<String>, Int>? = null,
): Int {
    val seen = mutableSetOf<String>()
    val queue = ArrayDeque<String>()
    queue += start
    seen += start

    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val edges = graph.getValue(current)
        for (e in edges) {
            if (e in seen) continue
            seen += e
            counter?.merge(setOf(current, e), 1, Int::plus)
            queue += e
        }
    }

    return seen.size
}
