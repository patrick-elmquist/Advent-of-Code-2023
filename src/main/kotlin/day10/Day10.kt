package day10

import common.day
import common.pointCharMap
import common.util.Point
import common.util.log
import common.util.neighbors

// answer #1: 6682
// answer #2:

fun main() {
    day(n = 10) {
        part1 { input ->
            val map = input.pointCharMap.filter { it.value != '.' }
            val start = map.entries.single { it.value == 'S' }

            val queue = mutableMapOf(start.key to 0)
            val distances = mutableMapOf<Point, Int>()
            distances[start.key] = 0
            while (queue.isNotEmpty()) {
                val (current, dist) = queue.minBy { it.value }
                queue.remove(current)

                if (distances.getOrDefault(current, Int.MAX_VALUE) < dist) {
                    continue
                }
                distances[current] = dist

                val (x, y) = current
                val neighbors = when (map[current]) {
                    '|' -> current.copy(y = y - 1) to current.copy(y = y + 1)
                    '-' -> current.copy(x = x - 1) to current.copy(x = x + 1)
                    'L' -> current.copy(y = y - 1) to current.copy(x = x + 1)
                    'J' -> current.copy(y = y - 1) to current.copy(x = x - 1)
                    '7' -> current.copy(x = x - 1) to current.copy(y = y + 1)
                    'F' -> current.copy(y = y + 1) to current.copy(x = x + 1)
                    'S' -> {
                        val (top, left, right, bottom) = current.neighbors().take(4).toList()
                        listOfNotNull(
                            top.takeIf { map[top] in listOf('|', '7', 'F') },
                            left.takeIf { map[left] in listOf('-', 'L', 'F') },
                            right.takeIf { map[right] in listOf('-', 'J', '7') },
                            bottom.takeIf { map[bottom] in listOf('|', 'L', 'J') },
                        ).let { (a, b) -> a to b }
                    }
                    else -> error("")
                }

                if (neighbors.first !in distances) {
                    queue += neighbors.first to dist + 1
                }
                if (neighbors.second !in distances) {
                    queue += neighbors.second to dist + 1
                }
            }

            distances.log()

            distances.values.max()
        }
        verify {
            expect result 6682
            run test 1 expect 4
            run test 2 expect 8
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}