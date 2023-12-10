package day10

import common.day
import common.pointCharMap
import common.util.Point
import common.util.log
import common.util.neighbors
import kotlin.math.abs
import kotlin.properties.Delegates.observable

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
                            left.takeIf { map[left] in listOf('-', 'L', 'F') },
                            bottom.takeIf { map[bottom] in listOf('|', 'L', 'J') },
                            right.takeIf { map[right] in listOf('-', 'J', '7') },
                            top.takeIf { map[top] in listOf('|', '7', 'F') },
                        ).let { (a, b) -> a to b }
                    }
                    else -> error("")
                }

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

private fun calculateAreaWithin(v: List<Point>): Int {
    val n = v.size
    var a = 0
    for (i in 0 until n - 1) {
        a += v[i].x * v[i + 1].y - v[i + 1].x * v[i].y
    }
    return (abs(a + v[n - 1].x * v[0].y - v[0].x * v[n -1].y) / 2f).toInt()
}

private fun calculateAreaWithin2(vertices: List<Point>): Int {
    val numberOfVertices = vertices.size
    var sum1 = 0
    var sum2 = 0

    for (i in 0 until numberOfVertices - 1) {
        sum1 += vertices[i].x * vertices[i + 1].y
        sum2 += vertices[i].y * vertices[i + 1].x
    }
    sum1 += vertices[numberOfVertices - 1].x * vertices[0].y
    sum2 += vertices[0].x * vertices[numberOfVertices - 1].y

    val area = abs(sum1 - sum2) / 2
    return area
}

/*
y, row ->
var open = false
var closeOnNext = false
var wasLastLoop = false
row.forEachIndexed { x, c ->
    val point = Point(x, y)
    if (point in loop) {
        if (wasLastLoop && open) {
            closeOnNext = true
        } else {
            open = !open
        }
        wasLastLoop = true
    } else {
        if (closeOnNext) {
            open = false
            closeOnNext = false
        }
        if (open) {
            point.log("Adding point")
            counted += point
            counter++
        }
        wasLastLoop = false
    }
}*/

//var open = false
//var x = 0
//while (x < row.length) {
//    val point = Point(x, y)
//    if (point in loop) {
//        if (x + 1 < row.length && point.copy(x = x + 1) in loop) {
//            var count = 0
//            // next is in loop as well, fast-forward
//            var current = point
//            while (current.x < row.length && current in loop) {
//                if (y == logRow) current.log("current")
//                current = current.copy(x = current.x +  1)
//                count++
//            }
//            repeat(count) {
//                open = !open
//            }
//            x = current.x
//            if (y == logRow) open.log("open state")
//            if (y == logRow) x.log("new x")
//        } else {
//            open = !open
//            if (y == logRow) open.log("open state")
//            x++
//        }
//    } else {
//        if (open) {
//            if (y == logRow) point.log("Counting point")
//            counted += point
//        } else {
//            if (y == logRow) log("ignored")
//        }
//        x++
//    }
//}
//val counted = mutableSetOf<Point>()
//val logRow = 5
//input.lines.forEachIndexed { y, row ->
//    val loopX = loop.filter { it.y == y }.map { it.x }
//    var open: Boolean by observable(false) { _, _, new ->
//        if (y == logRow) new.log("open:")
//    }
//    var x = 0
//    val count = mutableListOf<Int>()
//    while (x < row.length) {
//        if (y == logRow) row[x].log("char: ")
//        if (x in loopX) {
//            if (open) {
//                counted += count.map { Point(it, y) }
//                if (count.isNotEmpty()) {
//                    if (y == logRow) counted.log("counted changed: ")
//                }
//            }
//            count.clear()
//            if (y == logRow) "count cleared".log()
//            if (row[x] != '-') {
//                val openers = listOf('7', 'J')
//                val closers = listOf('L', 'F')
//                when (row[x]) {
//                    '|' -> open = !open
//                    in openers -> open = true
//                    in closers -> open = false
//                }
//            }
//        } else {
//            if (open) {
//                count += x
//                if (y == logRow) x.log("count updated with: ")
//            }
//        }
//        x++
//        if (y == logRow) println()
//    }
//}
