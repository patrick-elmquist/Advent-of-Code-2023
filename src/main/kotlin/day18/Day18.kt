package day18

import common.day
import common.util.Direction
import common.util.Point
import common.util.log
import common.util.nextInDirection

// answer #1: 58550
// answer #2: 47452118468566

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    day(n = 18) {
        part1 { input ->
            val (bounds, points) = input.lines
                .map { line -> line.split(" ").let { (a, b) -> a to b.toInt() } }
                .fold(0L to listOf(Point(0, 0))) { (bounds, points), (dir, len) ->
                    bounds + len to points + nextPoint(points.last(), dir, len)
                }
            calculateInnerArea(points).log("inner:") + bounds / 2L + 1L
        }
        verify {
            expect result 58550L
            run test 1 expect 62L
        }

        part2 { input ->
            val (bounds, points) = input.lines
                .map { it.split(" ").last().drop(2).dropLast(1) }
                .map { hex -> hex.takeLast(1) to hex.take(5).hexToInt() }
                .fold(0L to listOf(Point(0, 0))) { (bounds, points), (dir, len) ->
                    bounds + len to points + nextPoint(points.last(), dir, len)
                }
            calculateInnerArea(points) + bounds / 2L + 1L
        }
        verify {
            expect result 47452118468566L
            run test 1 expect 952408144115L
        }
    }
}

private fun nextPoint(source: Point, dir: String, distance: Int): Point {
    val direction = when (dir.first()) {
        '0', 'R' -> Direction.Right
        '1', 'D' -> Direction.Down
        '2', 'L' -> Direction.Left
        '3', 'U' -> Direction.Up
        else -> error("")
    }
    return source.nextInDirection(direction, steps = distance)
}

private fun calculateInnerArea(points: List<Point>): Long =
    points.map { listOf(it.x.toLong(), it.y.toLong()) }
        .zipWithNext()
        .sumOf { (p1, p2) -> p1[0] * p2[1] - p2[0] * p1[1] }
        .let { sum -> sum / 2 }
