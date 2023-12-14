package day14

import common.day
import common.pointCharMap
import common.util.Point

// answer #1: 108813
// answer #2: 104533

private enum class Direction { North, East, South, West }

fun main() {
    day(n = 14) {
        part1 { input ->
            val map = input.pointCharMap.toMutableMap()
            val height = input.lines.size
            solve(map, width = input.lines.first().length, height = input.lines.size)
            calculateValue(map, height)
        }
        verify {
            expect result 108813
            run test 1 expect 136
        }

        part2 { input ->
            val map = input.pointCharMap.toMutableMap()
            val height = input.lines.size
            val seenStates = mutableListOf<Pair<Int, Int>>()
            var counter = 0
            val n = 1_000_000_000
            val cycle = listOf(Direction.North, Direction.West, Direction.South, Direction.East)
            repeat(n) {
                cycle
                    .map { solve(map, width = input.lines.first().length, height = input.lines.size, direction = it) }
                    .last()
                val result = calculateValue(map, height)
                counter++
                val state = map.hashCode()
                val key = state to result
                if (key in seenStates) {
                    seenStates.add(key)
                    val loopDistance = findRepeating(seenStates)
                    if (loopDistance.isNotEmpty()) {
                        val left = n - 1 - (it + 1)
                        return@part2 loopDistance[left % loopDistance.size].second
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

private class DirectionComparator(private val direction: Direction) : Comparator<Pair<Point, Char>> {
    override fun compare(o1: Pair<Point, Char>, o2: Pair<Point, Char>): Int {
        val x = o1.first.x.compareTo(o2.first.x)
        val y = o1.first.y.compareTo(o2.first.y)
        return when (direction) {
            Direction.North -> y
            Direction.East -> -x
            Direction.South -> -y
            Direction.West -> x
        }
    }
}

private fun solve(
    map: MutableMap<Point, Char>,
    width: Int,
    height: Int,
    direction: Direction = Direction.North
) {
    val comparator = DirectionComparator(direction)
    map.filterValues { it == 'O' }
        .toList()
        .sortedWith(comparator)
        .forEach { (point, _) ->
            when (direction) {
                Direction.North -> {
                    rollRock(point, map, Direction.North, height, width)
                }

                Direction.East -> {
                    rollRock(point, map, Direction.East, height, width)
                }

                Direction.South -> {
                    rollRock(point, map, Direction.South, height, width)
                }

                Direction.West -> {
                    rollRock(point, map, Direction.West, height, width)
                }
            }
        }
}

private fun calculateValue(map: MutableMap<Point, Char>, height: Int) =
    map.filterValues { it == 'O' }.entries.toList()
        .sumOf { (point, _) -> height - point.y }

private fun rollRock(point: Point, map: MutableMap<Point, Char>, direction: Direction, height: Int, width: Int) {
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
    val copy = states.toMutableList()
    while (copy.isNotEmpty()) {
        val string = copy.joinToString(" ") { it.first.toString() }
        regex.find(string)?.destructured?.let {
            val result = it.match.groupValues.last().trim().split(" ")
            if (result.size > 1) {
                return states.takeLast(result.size)
            }
        }
        copy.removeFirst()
    }
    return emptyList()
}