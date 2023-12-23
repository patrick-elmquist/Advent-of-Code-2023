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
            grid.filter { it.value != '#' }.size log "valid size:"
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

                return paths.maxOf { it ->
                    rec(it, steps + 1, visited + node)
                }
            }

            rec(start.key, steps = 0, visited = emptySet())
        }
        verify {
            expect result 2334
            run test 1 expect 94
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect 154
        }
    }
}

private fun topologicalSort(grid: Map<Point, Char>, start: Point): Map<Point, List<Point>> {
    val nodeWithNeighbors = mutableMapOf<Point, List<Point>>()
    val visited = mutableSetOf<Point>()
    fun visit(node: Point) {
        if (node !in visited) {
            visited += node

            val neighbors = when (grid[node]) {
                '>' -> listOf(node.rightNeighbour)
                '<' -> listOf(node.leftNeighbour)
                '^' -> listOf(node.aboveNeighbour)
                'v' -> listOf(node.belowNeighbour)
                '.' -> node.neighbors().toList()
                else -> error("")
            }.filter { it in grid && grid[it] != '#' }

            neighbors.onEach { visit(it) }
            nodeWithNeighbors[node] = neighbors
        }
    }
    visit(start)
    return nodeWithNeighbors
}
