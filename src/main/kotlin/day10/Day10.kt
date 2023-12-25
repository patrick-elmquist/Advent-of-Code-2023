package day10

import common.Input
import common.day
import common.grid
import common.util.Point
import java.util.PriorityQueue
import kotlin.math.abs

// answer #1: 6682
// answer #2: 353

fun main() {
    day(n = 10) {
        part1 { input ->
            val (map, start) = parseMapAndStart(input)
            val path = mutableMapOf(start to 0)
            val queue = PriorityQueue<Point> { a, b -> path.getValue(a) - path.getValue(b) }
            queue.add(start)
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val dist = path.getValue(current)

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
            val path = mutableMapOf(start to 0)
            val queue = PriorityQueue<Point> { a, b -> path.getValue(a) - path.getValue(b) }
            queue.add(start)
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val dist = path.getValue(current)

                val candidates = getPathCandidates(map, current)
                if (current == start) {
                    // Only add first to get loop in order
                    queue += candidates.first()
                        .also { path[it] = dist + 1 }
                } else {
                    queue += candidates
                        .filter { it !in path }
                        .onEach { path[it] = dist + 1 }
                }
            }

            // Pick's Theorem
            // A = i + b/2 - 1  <>  i = A - b/2 + 1
            val loop = path.keys.toList()
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
    val map = input.grid.filter { it.value != '.' }
    val start = map.entries.single { it.value == 'S' }
    return map to start.key
}

private val left = Point(-1, 0)
private val right = Point(1, 0)
private val above = Point(0, -1)
private val below = Point(0, 1)
private fun getPathCandidates(
    map: Map<Point, Char>,
    p: Point,
): List<Point> {
    return when (map[p]) {
        '|' -> above to below
        '-' -> left to right
        'L' -> above to right
        'J' -> above to left
        '7' -> left to below
        'F' -> below to right
        'S' -> {
            listOfNotNull(
                left.takeIf { map[p + left] in listOf('-', 'L', 'F') },
                below.takeIf { map[p + below] in listOf('|', 'L', 'J') },
                right.takeIf { map[p + right] in listOf('-', 'J', '7') },
                above.takeIf { map[p + above] in listOf('|', '7', 'F') },
            ).let { (a, b) -> a to b }
        }
        else -> error("")
    }.toList().map { p + it }
}

private fun calculateGaussArea(v: List<Point>): Int {
    var a = 0
    for (i in 0..<v.size - 1) {
        a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
    }
    return abs(a + v.last().x * v.first().y - v.first().x * v.last().y) / 2
}
