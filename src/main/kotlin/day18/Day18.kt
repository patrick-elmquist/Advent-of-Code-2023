package day18

import common.Input
import common.day
import common.util.Direction
import common.util.Point
import common.util.nextInDirection

// answer #1: 58550
// answer #2: 47452118468566

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    day(n = 18) {
        part1 { input ->
            parseLines(input)
                .map { (dir, len, _) -> dir to len.toInt() }
                .calculateArea()
        }
        verify {
            expect result 58550L
            run test 1 expect 62L
        }

        part2 { input ->
            parseLines(input)
                .map { (_, _, hex) -> hex.takeLast(1) to hex.take(5).hexToInt() }
                .calculateArea()
        }
        verify {
            expect result 47452118468566L
            run test 1 expect 952408144115L
        }
    }
}

private val regex = """(\w) (\d+) \(#(\w+)\)""".toRegex()
private fun parseLines(input: Input) =
    input.lines.mapNotNull { line -> regex.matchEntire(line)?.groupValues?.drop(1) }

private fun List<Pair<String, Int>>.calculateArea(): Long =
    runningFold(Point(0, 0) to 0L) { (point, _), (dir, len) ->
        nextPoint(point, dir, len) to len.toLong()
    }.let { state ->
        val points = state.map { it.first }
        val boundary = state.sumOf { it.second }
        calculateInnerArea(points) + boundary / 2L + 1L
    }

private fun nextPoint(source: Point, dir: String, distance: Int): Point =
    source.nextInDirection(
        direction = when (dir.first()) {
            '0', 'R' -> Direction.Right
            '1', 'D' -> Direction.Down
            '2', 'L' -> Direction.Left
            '3', 'U' -> Direction.Up
            else -> error("")
        },
        steps = distance,
    )

private fun calculateInnerArea(points: List<Point>): Long =
    points.map { listOf(it.x.toLong(), it.y.toLong()) }
        .zipWithNext()
        .sumOf { (p1, p2) -> p1[0] * p2[1] - p2[0] * p1[1] }
        .let { sum -> sum / 2 }
