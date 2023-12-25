package day25

import common.Input
import common.day
import common.util.arrayDequeOf

// answer #1: 550080
// answer #2: -

fun main() {
    day(n = 25) {
        part1 { input ->
            val graph = parseGraph(input)

            val sortedEdges = countOccurrences(graph)
                .entries
                .sortedByDescending { (_, count) -> count }
                .map { (edge, _) -> edge }

            for (i in sortedEdges.indices) {
                for (j in i + 1..sortedEdges.lastIndex) {
                    for (k in j + 1..sortedEdges.lastIndex) {
                        val severedGraph = graph.cutEdges(sortedEdges[i], sortedEdges[j], sortedEdges[k])
                        val size = countSubGraphSize(severedGraph, start = graph.keys.first())
                        // assumes graph can at most be split in two
                        if (size < graph.size) {
                            return@part1 size * (graph.size - size)
                        }
                    }
                }
            }
        }
        verify {
            expect result 550080
            run test 1 expect 54
        }

        part2 {
            // noop
        }
        verify {
            expect result Unit
        }
    }
}

private fun parseGraph(input: Input): MutableMap<String, Set<String>> {
    val graph = mutableMapOf<String, Set<String>>()
    input.lines
        .map { line -> line.split(": ") }
        .associate { (v, edges) -> v to edges.split(" ").toSet() }
        .forEach { (v, edges) ->
            graph.merge(v, edges, Set<String>::plus)
            edges.forEach { v1 -> graph.merge(v1, setOf(v), Set<String>::plus) }
        }
    return graph
}

private typealias Edge = Set<String>
private fun edge(s: String, e: String) = setOf(s, e)

private fun countOccurrences(graph: Map<String, Edge>): Map<Edge, Int> {
    return buildMap {
        for (start in graph.keys) {
            countSubGraphSize(graph, start = start, edgeCountMap = this)
        }
    }
}

private fun Map<String, Edge>.cutEdges(vararg edgesToCut: Edge): Map<String, Edge> =
    this.mapValues { (node, destinations) ->
        val edgeToCut = edgesToCut.firstOrNull { node in it }
        if (edgeToCut == null) {
            destinations
        } else {
            val endNode = edgeToCut - node
            destinations - endNode
        }
    }

private fun countSubGraphSize(
    graph: Map<String, Edge>,
    start: String,
    edgeCountMap: MutableMap<Edge, Int>? = null,
): Int {
    val seen = mutableSetOf(start)
    val queue = arrayDequeOf(start)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        graph.getValue(current)
            .filter { it !in seen }
            .forEach { e ->
                edgeCountMap?.merge(edge(current, e), 1, Int::plus)
                seen += e
                queue += e
            }
    }
    return seen.size
}
