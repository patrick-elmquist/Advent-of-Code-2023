package day16

import common.day
import common.pointCharMap
import common.util.Point
import common.util.log

// answer #1: 7067
// answer #2:

private enum class Direction { Left, Up, Right, Down }

fun main() {
    day(n = 16) {
        part1 { input ->
            val map = input.pointCharMap
            val xRange = input.lines.first().indices
            val yRange = input.lines.indices

            val start = Point(0, 0)
            val startDirection = Direction.Right

            val energized = mutableSetOf<Point>()

            val memo = mutableSetOf<Pair<Point, Direction>>()
            val beams = mutableListOf(start to startDirection)
            while (beams.isNotEmpty()) {
                "new beam ${beams.first()}".log()
                val (point, direction) = beams.removeFirst()
                followBeam(
                    p = point,
                    d = direction,
                    energized = energized,
                    map = map,
                    beams = beams,
                    xRange = xRange,
                    yRange = yRange,
                    memo = memo,
                )
                for (y in yRange) {
                    for (x in xRange) {
                        if (Point(x, y) in energized) {
                            print('#')
                        } else {
                            print('.')
                        }
                    }
                    println()
                }
            }

            for (y in yRange) {
                for (x in xRange) {
                    if (Point(x, y) in energized) {
                        print('#')
                    } else {
                        print('.')
                    }
                }
                println()
            }
            map.keys.count { it in energized }
        }
        verify {
            expect result 7067
            run test 1 expect 46
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun followBeam(
    p: Point,
    d: Direction,
    energized: MutableSet<Point>,
    map: Map<Point, Char>,
    beams: MutableList<Pair<Point, Direction>>,
    xRange: IntRange,
    yRange: IntRange,
    memo: MutableSet<Pair<Point, Direction>>,
) {
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
        val directionOfNextTile = when {
            tile == '.' || tile == '-' && isHorizontal || tile == '|' && isVertical -> {
                "continue $direction".log()
                direction
            }

            tile == '\\' -> {
                when (direction) {
                    Direction.Left -> Direction.Up
                    Direction.Up -> Direction.Left
                    Direction.Right -> Direction.Down
                    Direction.Down -> Direction.Right
                }.also { "mirrored to $it".log() }
            }

            tile == '/' -> {
                when (direction) {
                    Direction.Left -> Direction.Down
                    Direction.Up -> Direction.Right
                    Direction.Right -> Direction.Up
                    Direction.Down -> Direction.Left
                }.also { "mirrored to $it".log() }
            }

            tile == '|' && isHorizontal -> {
                if (point.y - 1 in yRange) {
                    "adding beam UP after split".log()
                    beams += point.copy(y = point.y - 1) to Direction.Up
                }
                "following beam DOWN after split".log()
                Direction.Down
            }


            tile == '-' && isVertical -> {
                if (point.x - 1 in xRange) {
                    "adding beam LEFT after split".log()
                    beams += point.copy(x = point.x - 1) to Direction.Left
                }
                "following beam RIGHT after split".log()
                Direction.Right
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
}

