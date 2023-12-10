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

                distances[current] = dist

                val (candidateA, candidateB) = getPathCandidates(map, current)

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
            val path = mutableMapOf<Point, Int>()
            path[start.key] = 0
            while (queue.isNotEmpty()) {
                val (current, dist) = queue.minBy { it.value }
                queue.remove(current)

                path[current] = dist

                val neighbors = getPathCandidates(map, current)

                if (map[current] == 'S') {
                    // This is what makes it take the correct path
                    queue += neighbors.first to dist + 1
                } else {
                    if (neighbors.first !in path) {
                        queue += neighbors.first to dist + 1
                    }
                    if (neighbors.second !in path) {
                        queue += neighbors.second to dist + 1
                    }
                }
            }

            val loop = path.keys.toList()
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
    origin: Point,
): Pair<Point, Point> {
    val (x, y) = origin
    return when (map[origin]) {
        '|' -> Point(x, y - 1) to Point(x, y + 1)
        '-' -> Point(x - 1, y) to Point(x + 1, y)
        'L' -> Point(x, y = y - 1) to Point(x + 1, y)
        'J' -> Point(x, y = y - 1) to Point(x - 1, y)
        '7' -> Point(x - 1, y) to Point(x, y + 1)
        'F' -> Point(x, y + 1) to Point(x + 1, y)
        'S' -> {
            val (top, left, right, bottom) = origin.neighbors().toList()
            listOfNotNull(
                left.takeIf { map[left] in listOf('-', 'L', 'F') },
                bottom.takeIf { map[bottom] in listOf('|', 'L', 'J') },
                right.takeIf { map[right] in listOf('-', 'J', '7') },
                top.takeIf { map[top] in listOf('|', '7', 'F') },
            ).let { (a, b) -> a to b }
        }

        else -> error("")
    }
}

private fun calculateAreaWithin(v: List<Point>): Int {
    val n = v.size
    var a = 0
    for (i in 0 until n - 1) {
        a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
    }
    return (abs(a + v[n - 1].x * v[0].y - v[0].x * v[n - 1].y) / 2f).toInt()
}
