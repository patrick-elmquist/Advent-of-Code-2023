package day18

import common.day
import common.util.*

// answer #1: not 4292, too low
// answer #2:

fun main() {
    day(n = 18) {
        part1 { input ->
            val start = Point(0, 0)
            val originalMap = mutableListOf(start)
            input.lines.map { line ->
                val (dir, len, _) = line.split(" ")
                val distance = len.toInt()
                repeat(distance) {
                    val last = originalMap.last()
                    when (dir.first()) {
                        'U' -> originalMap.add(last.aboveNeighbour)
                        'D' -> originalMap.add(last.belowNeighbour)
                        'L' -> originalMap.add(last.leftNeighbour)
                        'R' -> originalMap.add(last.rightNeighbour)
                        else -> error("")
                    }
                }
            }

            val mapSet = originalMap.toMutableSet()
            val minMaxX = mapSet.minOf { it.x }..mapSet.maxOf { it.x }
            val minMaxY = mapSet.minOf { it.y }..mapSet.maxOf { it.y }

            val leftEdge = minMaxY.map { y -> Point(mapSet.filter { it.y == y }.minOf { it.x }, y) }
            val startPoint = leftEdge.first { it.rightNeighbour !in mapSet }.rightNeighbour.log("start:")
//            printMap(mapSet)
//            TODO()
            val queue = ArrayDeque<Point>()
            queue.add(startPoint)
            val visited = mutableSetOf<Point>()
            while (queue.isNotEmpty()) {
                val p = queue.removeFirst()
                if (p !in mapSet) {
                    mapSet.add(p)
                }
                if (p !in visited) {
                    visited += p
                    queue.addAll(
                        p.neighbors(diagonal = true)
                            .filter { it.x in minMaxX && it.y in minMaxY }
                            .filter { it !in mapSet }
                            .filter { it !in visited }
                    )
                }
            }

            println()
            printMap(mapSet)
            mapSet.size
        }
        verify {
            expect result null
//            run test 1 expect 62
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun printMap(
    mapSet: Set<Point>
) {
    val minMaxX = mapSet.minOf { it.x }..mapSet.maxOf { it.x }
    val minMaxY = mapSet.minOf { it.y }..mapSet.maxOf { it.y }
    for (y in minMaxY) {
        print("$y ".padStart(5, ' '))
        for (x in minMaxX) {
            val point = Point(x, y)
            if (point in mapSet) {
                print('#')
            } else {
                print('.')
            }
        }
        println()
    }
}