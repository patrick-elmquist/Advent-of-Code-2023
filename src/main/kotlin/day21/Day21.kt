package day21

import common.day
import common.grid
import common.pointCharMap
import common.util.*
import kotlin.collections.ArrayDeque

// answer #1: 3572
// answer #2:

fun main() {
    day(n = 21) {
        part1 { input ->
            val map = input.grid
            val start = map.entries.first { it.value == 'S' }.key
            findEndpoints(start, steps = 64, map)
        }
        verify {
            expect result 3572
        }

        part2 {

        }
        verify {
            expect result null
        }
    }
}

private fun findEndpoints(
    start: Point,
    steps: Int,
    map: Map<Point, Char>
): Int {
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
                .filter { map[it] == '.' }
                .forEach { next ->
                    visited += next
                    queue += next to nextStepsLeft
                }
        }
    }

    return possibleEndPoints.size
}