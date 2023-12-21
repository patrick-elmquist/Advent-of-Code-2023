package day21

import common.bounds
import common.day
import common.grid
import common.util.Point
import common.util.neighbors

// answer #1: 3572
// answer #2: 594606492802848

fun main() {
    day(n = 21) {
        part1 { input ->
            val grid = input.grid
            val start = grid.entries.first { it.value == 'S' }.key
            findEndPoints(grid, start, steps = 64)
        }
        verify {
            expect result 3572L
        }

        part2 { input ->
            val grid = input.grid
            val start = grid.entries.first { it.value == 'S' }.key
            val (width, height) = input.bounds

            val targetSteps = 26501365L
            val cycles = targetSteps / width
            val reminder = targetSteps % width
            val points = mutableListOf<Point>()

            var steps = 0
            var todo = setOf(start)
            repeat(3) { i ->
                while (steps < i * width + reminder) {
                    todo = todo.flatMap { point ->
                        point.neighbors()
                            .filter {
                                val wrapped = Point(
                                    x = (it.x % width + width) % width,
                                    y = (it.y % height + height) % height
                                )
                                grid[wrapped] != '#'
                            }
                    }.toSet()
                    steps++
                }
                points += Point(i, todo.size)
            }

            findTotal(cycles, points)
        }
        verify {
            expect result 594606492802848L
        }
    }
}

private fun findTotal(x: Long, points: List<Point>): Long {
    val (x1, y1) = points[0]
    val (x2, y2) = points[1]
    val (x3, y3) = points[2]
    return (x - x2) * (x - x3) / ((x1 - x2) * (x1 - x3)) * y1 +
            (x - x1) * (x - x3) / ((x2 - x1) * (x2 - x3)) * y2 +
            (x - x1) * (x - x2) / ((x3 - x1) * (x3 - x2)) * y3
}

private fun findEndPoints(
    map: Map<Point, Char>,
    start: Point,
    steps: Int
): Long {
    val visited = mutableSetOf(start)
    val possibleEndPoints = mutableSetOf<Point>()

    val queue = ArrayDeque<Pair<Point, Int>>()
    queue += start to steps

    while (queue.isNotEmpty()) {
        val (point, stepsLeft) = queue.removeFirst()

        if (stepsLeft % 2 == 0) possibleEndPoints += point
        if (stepsLeft == 0) continue

        val nextStepsLeft = stepsLeft - 1
        if (nextStepsLeft >= 0) {
            point.neighbors()
                .filter { map[it] == '.' && it !in visited }
                .forEach { next ->
                    visited += next
                    queue += next to nextStepsLeft
                }
        }
    }

    return possibleEndPoints.size.toLong()
}