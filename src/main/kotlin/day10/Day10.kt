package day10

import common.Input
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
            val (map, start) = parseMapAndStart(input)
            val queue = mutableListOf(start)
            val path = mutableMapOf(start to 0)
            while (queue.isNotEmpty()) {
                val current = queue.minBy { path.getValue(it) }
                val dist = path.getValue(current)
                queue.remove(current)

                queue += getPathCandidates(map, current)
                    .filter { it !in path }
                    .onEach { path[it] = dist + 1 }
            }
            path.values.max()
        }
        verify {
            expect result 6682
            run test 1 expect 4
            run test 2 expect 8
        }

        part2 { input ->
            val (map, start) = parseMapAndStart(input)
            val queue = mutableListOf(start)
            val path = mutableMapOf(start to 0)
            while (queue.isNotEmpty()) {
                val current = queue.minBy { path.getValue(it) }
                val dist = path.getValue(current)
                queue.remove(current)

                val candidates = getPathCandidates(map, current)
                if (current == start) {
                    // This is what makes it take the correct path
                    val candidate = candidates.first()
                    path[candidate] = dist + 1
                    queue += candidate
                } else {
                    queue += candidates
                        .filter { it !in path }
                        .onEach { path[it] = dist + 1 }
                }
            }

            val loop = path.keys.toList()
            // Pick's Theorem
            // A = i + b/2 - 1  <>  i = A - b/2 + 1
            calculateGaussArea(loop) - loop.size / 2 + 1
        }
        verify {
            expect result 353
            run test 3 expect 4
            run test 4 expect 4
            run test 5 expect 8
        }
    }
}

private fun parseMapAndStart(input: Input): Pair<Map<Point, Char>, Point> {
    val map = input.pointCharMap.filter { it.value != '.' }
    val start = map.entries.single { it.value == 'S' }
    return map to start.key
}

private fun getPathCandidates(
    map: Map<Point, Char>,
    origin: Point,
): List<Point> {
    val (x, y) = origin
    return when (map[origin]) {
        '|' -> Point(x, y - 1) to Point(x, y + 1)
        '-' -> Point(x - 1, y) to Point(x + 1, y)
        'L' -> Point(x, y - 1) to Point(x + 1, y)
        'J' -> Point(x, y - 1) to Point(x - 1, y)
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
    }.toList()
}

private fun calculateGaussArea(v: List<Point>): Int {
    var a = 0
    for (i in 0..<v.size - 1) {
        a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
    }
    return abs(a + v.last().x * v.first().y - v.first().x * v.last().y) / 2
}
