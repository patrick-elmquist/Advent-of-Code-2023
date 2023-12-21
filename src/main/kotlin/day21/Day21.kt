package day21

import common.*
import common.util.*
import kotlin.collections.ArrayDeque

// answer #1: 3572
// answer #2:

fun main() {
    day(n = 21) {
        part1 { input ->
            val map = input.grid
            val start = map.entries.first { it.value == 'S' }.key
            map.findEndPoints(start, steps = 64)
        }
        verify {
            expect result 3572L
        }

        part2 { input ->
            val map = input.grid
            val start = map.entries.first { it.value == 'S' }.key
            val size = input.bounds.first log "size"
            val steps = 26501365L

            assert(input.bounds.first == input.bounds.second)
            assert(start.x == start.y)
            assert(start.x == size / 2)
            assert((steps % size) == size / 2L)

            val width = (steps / size - 1) log "width"

            val odd = (width / 2 * 2 + 1).let { it * it } log "odd"
            val even = ((width + 1) / 2 * 2).let { it * it } log "even"

            // above is correct

            val oddPoints = map.findEndPoints(start, size * 2 + 1) log "odd points"

            val evenPoints = map.findEndPoints(start, size * 2) log "even points"

            val cornerSteps = size - 1
            val topCorner = map.findEndPoints(start.copy(y = size - 1), cornerSteps)
            val rightCorner = map.findEndPoints(start.copy(x = 0), cornerSteps)
            val bottomCorner = map.findEndPoints(start.copy(y = 0), cornerSteps)
            val leftCorner = map.findEndPoints(start.copy(x = size - 1), cornerSteps)

            val smallSteps = size / 2 - 1
            val smallTopRight = map.findEndPoints(Point(0, size - 1), smallSteps)
            val smallTopLeft = map.findEndPoints(Point(size - 1, size - 1), smallSteps)
            val smallBottomRight = map.findEndPoints(Point(0, 0), smallSteps)
            val smallBottomLeft = map.findEndPoints(Point(size - 1, 0), smallSteps)

            val largeSteps = size * 3 / 2 - 1
            val largeTopRight = map.findEndPoints(Point(0, size - 1), largeSteps)
            val largeTopLeft = map.findEndPoints(Point(size - 1, size - 1), largeSteps)
            val largeBottomRight = map.findEndPoints(Point(0, 0), largeSteps)
            val largeBottomLeft = map.findEndPoints(Point(size - 1, 0), largeSteps)

            odd * oddPoints +
                    even * evenPoints +
                    topCorner + rightCorner + bottomCorner + leftCorner +
                    (width + 1) * (smallTopRight + smallTopLeft + smallBottomRight + smallBottomLeft) +
                    width * (largeTopRight + largeTopLeft + largeBottomRight + largeBottomLeft)
            // not 594600652054873, too low
            // not 594606492802729, too low
        }
        verify {
            expect result null
        }
    }
}

private fun Map<Point, Char>.findEndPoints(
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
                .filter { it !in visited }
                .filter { this[it] == '.' }
                .forEach { next ->
                    visited += next
                    queue += next to nextStepsLeft
                }
        }
    }

    return possibleEndPoints.size.toLong()
}