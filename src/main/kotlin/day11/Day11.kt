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
            calculateSumOfDistances(input, expandMultiplier = 1)
        }
        verify {
            expect result 10292708L
            run test 1 expect 374L
        }

        part2 { input ->
            calculateSumOfDistances(input, expandMultiplier = 1_000_000 - 1)
        }
        verify {
            expect result 790194712336L
        }
    }
}

private fun calculateSumOfDistances(input: Input, expandMultiplier: Int): Long {
    val galaxies = input.lines.flatMapIndexed { y, row ->
        row.mapIndexedNotNull { x, c ->
            if (c == '#') Point(x, y) else null
        }
    }

    val minMaxX = galaxies.minOf(Point::x)..galaxies.maxOf(Point::x)
    val minMaxY = galaxies.minOf(Point::y)..galaxies.maxOf(Point::y)

    val emptyX = minMaxX - galaxies.map(Point::x).toSet()
    val emptyY = minMaxY - galaxies.map(Point::y).toSet()

    val expanded = galaxies.map { galaxy ->
        val offsetX = emptyX.count { it < galaxy.x } * expandMultiplier
        val offsetY = emptyY.count { it < galaxy.y } * expandMultiplier
        Point(galaxy.x + offsetX, galaxy.y + offsetY)
    }

    return expanded
        .flatMap { a -> expanded.map { b -> a.distance(b) } }
        .sum() / 2
}

private fun Point.distance(other: Point): Long =
    abs(other.x.toLong() - x.toLong()) + abs(other.y.toLong() - y.toLong())
