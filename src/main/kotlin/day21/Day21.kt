package day21

import common.day
import common.util.*
import java.util.*

// answer #1: 3572
// answer #2:

fun main() {
    day(n = 21) {
        part1 { input ->
            val steps = input.lines.first().toInt()
            val map = input.lines.drop(1).pointCharMap

            val start = map.entries.first { it.value == 'S' }.key

            val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
            distances[start] = 0

            val visited = mutableSetOf<Point>()

            val queue = PriorityQueue<Pair<Point, Int>> { o1, o2 ->
                distances.getValue(o1.first).compareTo(distances.getValue(o2.first))
            }
            queue.add(start to steps)

            while (queue.isNotEmpty()) {
                val (point, stepsLeft) = queue.poll()

                visited += point

                val nextStepsLeft = stepsLeft - 1
                if (nextStepsLeft >= 0) {
                    point.neighbors()
                        .filter { it !in visited }
                        .filter { map[it] == '.' }
                        .forEach { next ->
                            val dist = distances.getValue(next)
                            val newDist = distances.getValue(point) + 1
                            if (newDist < dist) {
                                distances[next] = newDist
                                queue += next to nextStepsLeft
                            }
                        }
                }
            }

            map.print { point, c ->
                if (point in distances) {
                    distances.getValue(point)
                } else {
                    c
                }
            }
            println()

            distances.entries
                .filter { it.value % 2 == 0 }
                .groupingBy { it.value }
                .eachCount().log()
                .values
                .sum()
        }
        verify {
            expect result 3572
            run test 1 expect 16
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}