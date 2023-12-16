package day16

import common.day
import common.pointCharMap
import common.util.Point

// answer #1: 7067
// answer #2: 7324

private enum class Direction { Left, Up, Right, Down }

fun main() {
    day(n = 16) {
        part1 { input ->
            val map = input.pointCharMap
            map.calculateEnergy(start = Point(x = 0, y = 0), startDirection = Direction.Right)
        }
        verify {
            expect result 7067
            run test 1 expect 46
        }

        part2 { input ->
            val map = input.pointCharMap
            val points = map.keys
            val lastRowIndex = input.lines.lastIndex
            val lastColIndex = input.lines.first().lastIndex
            val startingStates = listOf(
                points.filter { it.y == 0 }.map { it to Direction.Down },
                points.filter { it.y == lastRowIndex }.map { it to Direction.Up },
                points.filter { it.x == 0 }.map { it to Direction.Right },
                points.filter { it.x == lastColIndex }.map { it to Direction.Left }
            ).flatten()

            startingStates.maxOf { (start, startDirection) ->
                map.calculateEnergy(start = start, startDirection = startDirection)
            }
        }
        verify {
            expect result 7324
            run test 1 expect 51
        }
    }
}

private fun Map<Point, Char>.calculateEnergy(
    start: Point,
    startDirection: Direction,
): Int {
    val xRange = minOf { it.key.x }..maxOf { it.key.x }
    val yRange = minOf { it.key.y }..maxOf { it.key.y }
    val beams = mutableListOf(start to startDirection)
    val energized = mutableSetOf<Point>()
    val memo = mutableSetOf<Pair<Point, Direction>>()
    while (beams.isNotEmpty()) {
        val (point, direction) = beams.removeFirst()
        beams += followBeam(
            p = point,
            d = direction,
            energized = energized,
            map = this,
            xRange = xRange,
            yRange = yRange,
            memo = memo,
        )
    }
    return keys.count { it in energized }
}

private fun followBeam(
    p: Point,
    d: Direction,
    energized: MutableSet<Point>,
    map: Map<Point, Char>,
    memo: MutableSet<Pair<Point, Direction>>,
    xRange: IntRange,
    yRange: IntRange,
): List<Pair<Point, Direction>> {
    val splitBeams = mutableListOf<Pair<Point, Direction>>()
    var current = p to d
    while (current.first.let { it.x in xRange && it.y in yRange }) {
        val (point, direction) = current

        if (current in memo) {
            break
        }

        memo += current
        energized += point

        val tile = map.getValue(point)
        val isHorizontal = direction in setOf(Direction.Left, Direction.Right)
        val isVertical = direction in setOf(Direction.Up, Direction.Down)
        val proceed = tile == '.' || tile == '-' && isHorizontal || tile == '|' && isVertical
        val directionOfNextTile = when {
            proceed -> direction

            tile == '\\' -> when (direction) {
                Direction.Left -> Direction.Up
                Direction.Up -> Direction.Left
                Direction.Right -> Direction.Down
                Direction.Down -> Direction.Right
            }

            tile == '/' -> when (direction) {
                Direction.Left -> Direction.Down
                Direction.Up -> Direction.Right
                Direction.Right -> Direction.Up
                Direction.Down -> Direction.Left
            }

            tile == '-' && isVertical -> {
                if (point.x - 1 in xRange) {
                    splitBeams += point.copy(x = point.x - 1) to Direction.Left
                }
                Direction.Right
            }

            tile == '|' && isHorizontal -> {
                if (point.y - 1 in yRange) {
                    splitBeams += point.copy(y = point.y - 1) to Direction.Up
                }
                Direction.Down
            }

            else -> error("$point $direction")
        }

        current = when (directionOfNextTile) {
            Direction.Left -> point.copy(x = point.x - 1)
            Direction.Up -> point.copy(y = point.y - 1)
            Direction.Right -> point.copy(x = point.x + 1)
            Direction.Down -> point.copy(y = point.y + 1)
        } to directionOfNextTile
    }

    return splitBeams
}
