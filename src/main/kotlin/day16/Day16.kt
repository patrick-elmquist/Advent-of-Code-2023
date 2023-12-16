package day16

import common.day
import common.pointCharMap
import common.util.*

// answer #1: 7067
// answer #2: 7324

fun main() {
    day(n = 16) {
        part1 { input ->
            countEnergizedTiles(
                map = input.pointCharMap,
                initPoint = Point(0, 0),
                initDirection = Direction.Right
            )
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
                countEnergizedTiles(
                    map = map,
                    initPoint = start,
                    initDirection = startDirection
                )
            }
        }
        verify {
            expect result 7324
            run test 1 expect 51
        }
    }
}

private fun countEnergizedTiles(
    map: Map<Point, Char>,
    initPoint: Point,
    initDirection: Direction,
): Int {
    val beams = mutableListOf(initPoint to initDirection)
    val seenStates = mutableSetOf<Pair<Point, Direction>>()
    while (beams.isNotEmpty()) {
        val (point, direction) = beams.removeFirst()
        beams += map.followBeam(
            fromPoint = point,
            fromDirection = direction,
            seenStates = seenStates,
        )
    }
    return seenStates.distinctBy { it.first }.size
}

private fun Map<Point, Char>.followBeam(
    fromPoint: Point,
    fromDirection: Direction,
    seenStates: MutableSet<Pair<Point, Direction>>,
): List<Pair<Point, Direction>> {
    val splitBeams = mutableListOf<Pair<Point, Direction>>()
    var current = fromPoint to fromDirection
    while (current.first in this) {
        val (point, direction) = current

        if (current in seenStates) {
            break
        }

        seenStates += current

        val tile = getValue(point)
        val canContinueForward = tile == '.' ||
                tile == '-' && direction.isHorizontal ||
                tile == '|' && direction.isVertical

        val newDirection = when {
            canContinueForward -> direction

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

            tile == '-' && direction.isVertical -> {
                if (point.leftNeighbour in this) {
                    splitBeams += point.leftNeighbour to Direction.Left
                }
                Direction.Right
            }

            tile == '|' && direction.isHorizontal -> {
                if (point.aboveNeighbour in this) {
                    splitBeams += point.aboveNeighbour to Direction.Up
                }
                Direction.Down
            }

            else -> error("$point $direction")
        }

        current = point.neighborInDirection(newDirection) to newDirection
    }

    return splitBeams
}
