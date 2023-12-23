package day23

import common.bounds
import common.day
import common.grid
import common.util.*

// answer #1: 2334
// answer #2: 6422

fun main() {
    day(n = 23) {
        part1 { input ->
            val grid = input.grid
            val (_, height) = input.bounds
            val start = grid.entries.first { it.key.y == 0 && it.value == '.' }.key
            val end = grid.entries.first { it.key.y == height - 1 && it.value == '.' }.key
            findMax(
                current = start,
                end = end,
                getNeighbours = { node ->
                    when (grid[node]) {
                        '>' -> listOf(node.rightNeighbour to 1)
                        '<' -> listOf(node.leftNeighbour to 1)
                        '^' -> listOf(node.aboveNeighbour to 1)
                        'v' -> listOf(node.belowNeighbour to 1)
                        else -> grid.getNeighbours(node).map { it to 1 }.toList()
                    }
                }
            )
        }
        verify {
            expect result 2334
            run test 1 expect 94
        }

        part2 { input ->
            val grid = input.grid
            val (_, height) = input.bounds
            val start = grid.entries.first { it.key.y == 0 && it.value == '.' }.key
            val end = grid.entries.first { it.key.y == height - 1 && it.value == '.' }.key

            val junctions = mutableMapOf<Point, MutableList<Pair<Point, Int>>>(
                start to mutableListOf(),
                end to mutableListOf(),
            )

            grid.filter { (_, c) -> c == '.' }
                .filter { (point, _) -> grid.getNeighbours(point).size > 2 }
                .forEach { (point, _) -> junctions[point] = mutableListOf() }

            junctions.keys.forEach { junction ->
                var current = setOf(junction)
                val visited = mutableSetOf(junction)
                var distance = 0

                while (current.isNotEmpty()) {
                    distance++
                    val updatedSet = mutableSetOf<Point>()
                    current.flatMap { grid.getNeighbours(it) }
                        .filter { it !in visited }
                        .forEach { n ->
                            if (n in junctions) {
                                junctions.getValue(junction).add(n to distance)
                            } else {
                                updatedSet += n
                                visited += n
                            }
                        }
                    current = updatedSet
                }
            }

            findMax(current = start, end = end, getNeighbours = junctions::getValue)
        }
        verify {
            expect result 6422
            run test 1 expect 154
        }
    }
}

private fun findMax(
    current: Point,
    end: Point,
    visited: MutableSet<Point> = mutableSetOf(),
    distance: Int = 0,
    getNeighbours: (Point) -> List<Pair<Point, Int>>,
): Int {
    if (current == end) return distance

    visited += current
    val max = getNeighbours(current)
        .filter { (neighbour, _) -> neighbour !in visited }
        .maxOfOrNull { (neighbour, weight) ->
            findMax(
                current = neighbour,
                end = end,
                visited = visited,
                distance = distance + weight,
                getNeighbours = getNeighbours,
            )
        }
    visited -= current

    return max ?: 0
}

private fun Map<Point, Char>.getNeighbours(point: Point): List<Point> =
    point.neighbors().filter { it in this && getValue(it) in ".<>^v" }.toList()
