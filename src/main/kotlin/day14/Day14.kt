package day14

import common.day
import common.pointCharMap
import common.util.Point
import common.util.log
import kotlin.math.round

// answer #1: 108813
// answer #2:

fun main() {
    day(n = 14) {
        part1 { input ->
            val map = input.pointCharMap.toMutableMap()

            val roundRocks = map.filterValues { it == 'O' }
                .toList()
                .sortedWith { o1, o2 ->
                    val y = o1.first.y.compareTo(o2.first.y)
                    if (y == 0) {
                        o1.first.x.compareTo(o2.first.x)
                    } else {
                        y
                    }
                }

            roundRocks.log("round: ")
            roundRocks.forEach { (point, c) ->
                val progression = point.y - 1 downTo 0
                progression.log("prog:")
                val newY = progression.takeWhile {
                    map.getValue(Point(point.x, it)) == '.'
                }.log("candidates:").lastOrNull()
                if (newY != null) {
                    newY.log("point: $point")
                    map[point] = '.'
                    map[Point(point.x, newY)] = 'O'
                }
            }

            val height = input.lines.size
            map.filterValues { it == 'O' }.entries.toList()
                .sumOf { (point, c) ->
                    height - point.y
                }
        }
        verify {
            expect result 108813
            run test 1 expect 136
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun Map<Point, Char>.print() {
    val minMaxX: IntRange = minOf { it.key.x }..maxOf { it.key.x }
    val minMaxY: IntRange = minOf { it.key.y }..maxOf { it.key.y }

    for (y in minMaxY) {
        for (x in minMaxX) {
            print(this[Point(x, y)])
        }
        println()
    }
    println()
}
