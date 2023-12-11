package day11

import common.day
import common.util.Point
import common.util.log
import kotlin.math.abs

// answer #1: 10292708
// answer #2:

fun main() {
    day(n = 11) {
        part1 { input ->
            val galaxies = mutableListOf<Point>()
            input.lines.forEachIndexed { y, row ->
                row.forEachIndexed { x, c ->
                    if (c == '#') {
                        galaxies.add(Point(x, y))
                    }
                }
            }
            val minX = galaxies.minOf { it.x }
            val maxX = galaxies.maxOf { it.x }
            val minY = galaxies.minOf { it.y }
            val maxY = galaxies.maxOf { it.y }

            val emptyX = (minX..maxX) - galaxies.map { it.x }.toSet()
            val emptyY = (minY..maxY) - galaxies.map { it.y }.toSet()

            val moved = galaxies.map {
                val (x, y) = it
                val offsetX = emptyX.count { it < x }
                val offsetY = emptyY.count { it < y }
                Point(x + offsetX, y + offsetY)
            }

            emptyX.log()
            emptyY.log()

            val distances = mutableMapOf<Pair<Point, Point>, Int>()
            moved.forEach { a ->
                moved.forEach { b ->
                    if (b == a) {

                    } else {
                        if (a.x == b.x) {
                           if (a.y < b.y) {
                               distances.put(a to b, a.distance(b))
                           } else {
                               distances.put(b to a, a.distance(b))
                           }
                        } else if (a.x < b.x) {
                            distances.put(a to b, a.distance(b))
                        } else {
                            distances.put(b to a, a.distance(b))
                        }
                    }
                }
            }
            distances.size.log("dist")

            distances.values.sum()
        }

        verify {
            expect result 10292708
            run test 1 expect 374
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun Point.distance(other: Point): Int {
    return abs(other.x - x) + abs(other.y - y)
}

