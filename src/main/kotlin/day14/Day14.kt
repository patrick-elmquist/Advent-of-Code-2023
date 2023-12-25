package day14

import common.day
import common.grid
import common.util.Point

// answer #1: 108813
// answer #2: 104533

fun main() {
    day(n = 14) {
        part1 { input ->
            val map = input.grid.toMutableMap()
            val height = input.lines.size
            map.tilt(direction = Direction.North)
            calculateValue(map, height)
        }
        verify {
            expect result 108813
            run test 1 expect 136
        }

        part2 { input ->
            val map = input.grid.toMutableMap()
            val height = input.lines.size
            val seenStates = mutableListOf<Pair<Int, Int>>()
            val n = 1_000_000_000
            val cycle = listOf(Direction.North, Direction.West, Direction.South, Direction.East)
            repeat(n) {
                cycle.forEach { direction -> map.tilt(direction = direction) }

                val result = calculateValue(map, height)
                val key = map.hashCode() to result
                if (key in seenStates) {
                    seenStates.add(key)
                    val loop = findRepeating(seenStates)
                    if (loop.isNotEmpty()) {
                        val left = n - 1 - (it + 1)
                        return@part2 loop[left % loop.size].second
                    }
                } else {
                    seenStates.add(key)
                }
            }
        }
        verify {
            expect result 104533
            run test 1 expect 64
        }
    }
}

private enum class Direction { North, East, South, West }

private class DirectionComparator(private val direction: Direction) : Comparator<Point> {
    override fun compare(o1: Point, o2: Point): Int {
        val x = o1.x.compareTo(o2.x)
        val y = o1.y.compareTo(o2.y)
        return when (direction) {
            Direction.North -> y
            Direction.East -> -x
            Direction.South -> -y
            Direction.West -> x
        }
    }
}

private fun MutableMap<Point, Char>.tilt(direction: Direction) {
    val width = maxOf { it.key.x } + 1
    val height = maxOf { it.key.y } + 1
    val comparator = DirectionComparator(direction)
    filterValues { it == 'O' }
        .keys
        .sortedWith(comparator)
        .forEach { rock -> moveRock(rock, this, direction, height, width) }
}

private fun calculateValue(map: MutableMap<Point, Char>, height: Int) =
    map.filterValues { it == 'O' }.entries.toList()
        .sumOf { (point, _) -> height - point.y }

private fun moveRock(point: Point, map: MutableMap<Point, Char>, direction: Direction, height: Int, width: Int) {
    val range = when (direction) {
        Direction.North -> point.y - 1 downTo 0
        Direction.East -> point.x + 1..<width
        Direction.South -> point.y + 1..<height
        Direction.West -> point.x - 1 downTo 0

    }
    range.asSequence()
        .map {
            when (direction) {
                Direction.North,
                Direction.South -> point.copy(y = it)
                Direction.East,
                Direction.West -> point.copy(x = it)
            }
        }
        .takeWhile { newPoint -> map[newPoint] == '.' }
        .lastOrNull()
        ?.let { newPoint ->
            map[point] = '.'
            map[newPoint] = 'O'
        }
}

private val regex = """(.+?)\1+$""".toRegex()
private fun findRepeating(states: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    val string = states.joinToString(" ") { it.first.toString() }
    regex.find(string)?.destructured?.let {
        val result = it.match.groupValues.last().trim().split(" ")
        if (result.size > 1) {
            return states.takeLast(result.size)
        }
    }
    return emptyList()
}