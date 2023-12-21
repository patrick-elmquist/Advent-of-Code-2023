package day21

import common.*
import common.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil

// answer #1: 3572
// answer #2:
// not 594600652054873, too low
// not 594606492802729, too low
// not 76856573672052
// not 677521293049856
// not 677507839268820
// not 677514537387279

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
            val steps = 26501365L
            val (width, height) = input.bounds
            val mod = steps % height

            val list = listOf(mod, mod + height, mod + height * 2)

            val seenStates = mutableListOf<Long>()
            list.forEach { run ->
                val nextQueue = mutableListOf(start)

                for (a in 0..<run) {
                    val currentQueue = nextQueue.toMutableList()
                    val visited = nextQueue.toMutableSet()
                    nextQueue.clear()

                    while (currentQueue.isNotEmpty()) {
                        val curr = currentQueue.removeFirst()
                        curr.neighbors()
                            .forEach { dir ->
                                val (newX, newY) = dir
                                if (
                                    map[Point(x = newX % width, y = newY % height)] != '#' &&
                                    dir !in visited
                                ) {
                                    visited += dir
                                    nextQueue += dir
                                }
                            }
                    }
                }

                seenStates += nextQueue.size.toLong()
            }

            seenStates.log()
            val m = seenStates[1] - seenStates[0]
            val n = seenStates[2] - seenStates[1]
            val a = (n - m) / 2
            val b = m - 3 * a
            val c = seenStates[0] - b - a

            val ceiling = 26501365L / height log "ceil"

            (a * ceiling * ceiling + b * ceiling + c).toLong()
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