package day11

import common.Input
import common.day
import common.util.Point
import kotlin.math.abs

// answer #1: 10292708
// answer #2: 790194712336

fun main() {
    day(n = 11) {
        part1 { input ->
            calculateDistances(input, n = 1).sum()
        }
        verify {
            expect result 10292708L
            run test 1 expect 374L
        }

        part2 { input ->
            calculateDistances(input, n = 1_000_000 - 1).sum()
        }
        verify {
            expect result 790194712336L
        }
    }
}

private fun calculateDistances(input: Input, n: Int): List<Long> {
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

    val moved = galaxies.map { galaxy ->
        val (x, y) = galaxy
        val offsetX = emptyX.count { it < x } * n
        val offsetY = emptyY.count { it < y } * n
        Point(x + offsetX, y + offsetY)
    }

    val distances = mutableMapOf<Set<Point>, Long>()
    moved.forEach { a ->
        moved.forEach { b ->
            if (b != a) {
                distances[setOf(a, b)] = a.distance(b)
            }
        }
    }

    return distances.values.toList()
}

private fun Point.distance(other: Point): Long {
    return abs(other.x.toLong() - x.toLong()) + abs(other.y.toLong() - y.toLong())
}
