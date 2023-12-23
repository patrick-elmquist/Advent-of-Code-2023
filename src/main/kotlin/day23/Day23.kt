package day23

import common.bounds
import common.day
import common.grid
import common.util.*

// answer #1: 2334
// answer #2:

fun main() {
    day(n = 23) {
        part1 { input ->
            val grid = input.grid
            val (_, height) = input.bounds
            val start = grid.entries.filter { it.key.y == 0 }.single { it.value == '.' }
            val end = grid.entries.filter { it.key.y == height - 1 }.single { it.value == '.' }

            val nodeWithNeighbors = topologicalSort(grid, start.key)

            fun rec(node: Point, steps: Int, visited: Set<Point>): Int {
                if (node == end.key) {
                    return steps
                }

                val paths = nodeWithNeighbors.getValue(node)
                    .filter { it !in visited }

                if (paths.isEmpty()) {
                    return Int.MIN_VALUE
                }

                return paths.maxOf {
                    rec(it, steps + 1, visited + node)
                }
            }

            rec(start.key, steps = 0, visited = emptySet())
        }
        verify {
            expect result 2334
            run test 1 expect 1 // fail to avoid running 1 94
        }

        part2 { input ->
            val grid = input.grid
            val (_, height) = input.bounds
            val start = grid.entries.filter { it.key.y == 0 }
                .single { it.value == '.' }
            val end = grid.entries.filter { it.key.y == height - 1 }.single { it.value == '.' }

            fun calculateSegment(start: Point, last: Point): List<Point> {
                val queue = ArrayDeque<Point>()
                queue += start
                val segment = mutableListOf<Point>()
                var prev = last
                while (queue.isNotEmpty()) {
                    val point = queue.removeFirst()

                    segment += point

                    val neighbors = point.neighbors()
                        .filter { it in grid && grid.getValue(it) != '#' }
                        .toList()

                    if (neighbors.count { grid[it] in setOf('<', '>', 'v', '^') } > 2) {
                        "$point breaking due to $neighbors".log()
                        break
                    } else {
                        "$point $neighbors".log()
                        neighbors.filter { it != prev }.firstOrNull()?.let {
                            queue += it
                        }
                        prev = point
                    }
                }
                return segment
            }

            fun calculateSegments(start: Point, end: Point): Map<Point, List<Point>> {
                val segments = mutableMapOf<Point, List<Point>>()
                val queue = ArrayDeque<Pair<Point, Point>>()
                queue += start to start.aboveNeighbour
                val visited = mutableSetOf<Point>()

                while (queue.isNotEmpty()) {
                    val (node, last) = queue.removeFirst()
                    "CHECK $node ($last)".log()

                    if (node in visited) continue
                    if (node == end) continue

                    val segment = calculateSegment(node, last)
                    segments += node to (listOf(last) + segment)
                    visited += segment

                    queue += segment.last().neighbors()
                        .filter { it in grid && grid[it] != '#' }
                        .filter { it !in visited }
                        .map { it to segment.last() }

                    queue log "queue after $node"
                    segments log "segments after $node"
                    println()
                }

                return segments
            }

            grid
                .filter { it.value != '#' }
                .count {
                    it.key.neighbors()
                        .filter { it in grid && grid.getValue(it) != '#' }
                        .count()
                        .apply {
                            if (this > 2) {
                                "n: ${it.key} = $this".log()
                            }
                        } > 2
                } log "n > 2:"
            println()
            "segments".log()
            val segments = calculateSegments(start.key, end.key)
                .also {
                    it.forEach { (start, segment) ->
                        "start:$start (${segment.size})".log()
                        segment.forEach {
                            it.log()
                        }
                        println()
                    }
                    it.size log "size:"
                }

            val startSegment = segments[start.key] log "start segment"
            val segmentLen = segments.mapValues { it.value.size }
            val segmentStartEnd = segments.mapValues { it.value.last() }.toMap()

            fun rec(segment: List<Point>, visited: Set<List<Point>>): List<Point> {
                if (segment.last() == end.key) return segment

                val visit = visited + setOf(segment)
                return segment + (segments.values
                    .filter { it.first() == segment.last() }
                    .filter { it !in visit}
                    .map {
                        val rec = rec(it, visit)
                        rec.size log "check ${segment.first()}"
                        rec
                    }
                    .maxByOrNull { it.size }.also { "returning".log() } ?: emptyList())
            }

//            val points = segments.entries
//                .filter { it.value.first() == segments.getValue(start.key).last() }
//                .onEach { it.value.size log "size" }
//                .first().value
            val points = rec(segments.getValue(start.key), emptySet()) log "rec"
            points.size log "rec size"

            grid.print { point, c ->
                if (point in points) {
                    'O'
                } else {
                    c
                }
            }

            TODO()
        }
        verify {
            expect result null
            run test 1 expect 154
        }
    }
}

private fun getNeighbors(point: Point): List<Pair<Point, Direction>> {
    return listOf(
        point.aboveNeighbour to Direction.Up,
        point.belowNeighbour to Direction.Down,
        point.leftNeighbour to Direction.Left,
        point.rightNeighbour to Direction.Right,
    )
}

private fun topologicalSort(
    grid: Map<Point, Char>,
    start: Point,
    ignoreSlopes: Boolean = false
): Map<Point, List<Point>> {
    val nodeWithNeighbors = mutableMapOf<Point, List<Point>>()
    val visited = mutableSetOf<Point>()
    fun visit(node: Point) {
        if (node !in visited) {
            visited += node

            val potentialNextSteps = if (ignoreSlopes) {
                node.neighbors().toList()
            } else {
                when (grid[node]) {
                    '>' -> listOf(node.rightNeighbour)
                    '<' -> listOf(node.leftNeighbour)
                    '^' -> listOf(node.aboveNeighbour)
                    'v' -> listOf(node.belowNeighbour)
                    '.' -> node.neighbors().toList()
                    else -> error("")
                }
            }

            val neighbors = potentialNextSteps
                .filter { it in grid && grid[it] != '#' }

            neighbors.onEach { visit(it) }
            nodeWithNeighbors[node] = neighbors
        }
    }
    visit(start)
    return nodeWithNeighbors
}
