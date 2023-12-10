package day10

import common.day
import common.pointCharMap
import common.util.Point
import common.util.neighbors
import kotlin.math.abs

// answer #1: 6682
// answer #2: 353

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
                val (candidateA, candidateB) = getPathCandidates(map, current, y, x)

                if (candidateA !in distances) {
                    queue += candidateA to dist + 1
                }
                if (candidateB !in distances) {
                    queue += candidateB to dist + 1
                }
            }
            distances.values.max()
        }
        verify {
            expect result 6682
            run test 1 expect 4
            run test 2 expect 8
        }

        part2 { input ->
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
                val neighbors = getPathCandidates(map, current, y, x)

                if (map[current] == 'S') {
                    // This is what makes it take the correct path
                    queue += neighbors.first to dist + 1
                } else {
                    if (neighbors.first !in distances) {
                        queue += neighbors.first to dist + 1
                    }
                    if (neighbors.second !in distances) {
                        queue += neighbors.second to dist + 1
                    }
                }
            }

            val loop = distances.keys.toList()
            // Pick's Theorem
            // A = i + b/2 - 1
            calculateAreaWithin(loop) - loop.size / 2 + 1
        }
        verify {
            expect result 353
            run test 3 expect 4
            run test 4 expect 4
            run test 5 expect 8
        }
    }
}

private fun getPathCandidates(
    map: Map<Point, Char>,
    current: Point,
    y: Int,
    x: Int
) = when (map[current]) {
    '|' -> current.copy(y = y - 1) to current.copy(y = y + 1)
    '-' -> current.copy(x = x - 1) to current.copy(x = x + 1)
    'L' -> current.copy(y = y - 1) to current.copy(x = x + 1)
    'J' -> current.copy(y = y - 1) to current.copy(x = x - 1)
    '7' -> current.copy(x = x - 1) to current.copy(y = y + 1)
    'F' -> current.copy(y = y + 1) to current.copy(x = x + 1)
    'S' -> {
        val (top, left, right, bottom) = current.neighbors().take(4).toList()
        listOfNotNull(
            left.takeIf { map[left] in listOf('-', 'L', 'F') },
            bottom.takeIf { map[bottom] in listOf('|', 'L', 'J') },
            right.takeIf { map[right] in listOf('-', 'J', '7') },
            top.takeIf { map[top] in listOf('|', '7', 'F') },
        ).let { (a, b) -> a to b }
    }

    else -> error("")
}

private fun calculateAreaWithin(v: List<Point>): Int {
    val n = v.size
    var a = 0
    for (i in 0 until n - 1) {
        a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
    }
    return (abs(a + v[n - 1].x * v[0].y - v[0].x * v[n -1].y) / 2f).toInt()
}
