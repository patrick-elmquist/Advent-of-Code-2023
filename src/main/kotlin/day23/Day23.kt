package day23

import common.Input
import common.bounds
import common.day
import common.grid
import common.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.supervisorScope
import kotlin.math.max

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
            val nodeWithNeighbors = mutableMapOf<Point, List<Point>>()

            fun topologicalSort(grid: Map<Point, Char>): List<Point> {
                val visited = mutableSetOf<Point>()
                val sorted = mutableListOf<Point>()
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

                        neighbors
                            .onEach { visit(it) }

                        sorted += node
                        nodeWithNeighbors[node] = neighbors
                    }
                }

                visit(start.key)
                return sorted.reversed() log "sorted"
            }
            topologicalSort(grid).also { it.size log "sorted size"}
            nodeWithNeighbors log "nodes"

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
            run test 1 expect Unit
        }
    }
}

private fun getDirections(p: Point): List<Pair<Direction, Point>> =
    listOf(
        Direction.Left to p.leftNeighbour,
        Direction.Up to p.aboveNeighbour,
        Direction.Right to p.rightNeighbour,
        Direction.Down to p.belowNeighbour,
    )

private fun dfs(
    node: Point,
    grid: Map<Point, Char>,
    dp: MutableMap<Point, Int>,
    visited: MutableSet<Point>
) {
    visited += node

    getDirections(node)
        .filter { (_, point) -> grid[point] == '.' }
        .filter { (dir, _) ->
            when (grid.getValue(node)) {
                '^' -> dir == Direction.Up
                'v' -> dir == Direction.Down
                '<' -> dir == Direction.Left
                '>' -> dir == Direction.Right
                else -> true
            }
        }
        .forEach { (_, point) ->
            if (point !in visited) {
                dfs(point, grid, dp, visited)
            }
            dp.merge(node, 1 + dp.getValue(point), ::max)
        }
}

private fun findLongestPath(input: Input): Int {
    val grid = input.grid
    val (_, height) = input.bounds
    val start = grid.entries.filter { it.key.y == 0 }.single { it.value == '.' }
    val end = grid.entries.filter { it.key.y == height - 1 }.single { it.value == '.' }
    val dp = mutableMapOf<Point, Int>().withDefault { 0 }
//    dp[start.key] = 0

    val visited = mutableSetOf<Point>()
//    visited += start.key

    getDirections(start.key)
        .filter { grid[it.second] == '.' }
        .forEach { node ->
            if (node.second !in visited) {
                dfs(node.second, grid, dp, visited)
            }
        }

    dp.log()
    dp.maxBy { it.key.x } log "max x"
    dp.maxBy { it.key.y } log "max y"
    return dp.maxOf { it.value }
}

private fun firstAttempt(
    start: Map.Entry<Point, Char>,
    grid: Map<Point, Char>,
    end: Map.Entry<Point, Char>
): Int {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MIN_VALUE }
    distances += start.key to 0

    val queue = ArrayDeque<Point>()
    queue += start.key

    val visited = mutableSetOf<Point>()
    visited += start.key

    fun getDirections(p: Point): List<Pair<Direction, Point>> =
        listOf(
            Direction.Left to p.leftNeighbour,
            Direction.Up to p.aboveNeighbour,
            Direction.Right to p.rightNeighbour,
            Direction.Down to p.belowNeighbour,
        )

    while (queue.isNotEmpty()) {
        val p = queue.removeFirst()
        val distance = distances.getValue(p)

        getDirections(p)
            .filter { it.second in grid }
            .filter { it.second !in visited }
            .filter { (dir, point) ->
                when (grid.getValue(point)) {
                    '#' -> false
                    '.' -> true
                    '^' -> dir == Direction.Up
                    'v' -> dir == Direction.Down
                    '<' -> dir == Direction.Left
                    '>' -> dir == Direction.Right
                    else -> error("")
                }
            }
            .forEach { (_, point) ->
                distances.merge(point, distance + 1, ::max)
                visited += point
                queue += point
            }
    }

    grid.print { point, c ->
        if (point in distances) {
            distances.getValue(point) % 10
        } else {
            c
        }
    }
    println()

    return distances.getValue(end.key)
}