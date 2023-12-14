package day14

import common.day
import common.pointCharMap
import common.util.Point
import common.util.log
import common.util.match
import io.ktor.client.plugins.*

// answer #1: 108813
// answer #2: 104533

private enum class Direction { North, East, South, West }

fun main() {
    day(n = 14) {
        part1 { input ->
            val map = input.pointCharMap.toMutableMap()
            solve(map, width = input.lines.first().length, height = input.lines.size)
        }
        verify {
            expect result 108813
            run test 1 expect 136
        }

        part2 { input ->
            val map = input.pointCharMap.toMutableMap()
            val seenStates = mutableListOf<Pair<Int, Int>>()
            var counter = 0
            val n = 1_000_000_000
            var result = 0
            var finalResult = 0
            repeat(n) {
                listOf(Direction.North, Direction.West, Direction.South, Direction.East).forEach {
                    result = solve(map, width = input.lines.first().length, height = input.lines.size, direction = it)
                }
                counter++
                val state = map.hashCode()
                "$counter $state".log()
                val key = state to result
                if (key in seenStates) {
                    seenStates.add(key)
                    val loopDistance = findRepeating(seenStates)
                    if (loopDistance.isNotEmpty()) {
                        val left = n - counter - 1
                        left.log("left: ")
                        finalResult = loopDistance[left % loopDistance.size].second
                        return@part2 finalResult
//                        TODO("$counter FOUND loop distance $loopDistance")
                    }
                } else {
                    seenStates.add(key)
                }

                if (it % 1_000_000 == 0) {
//                    println(it)
                }
            }
            result
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
): Int {
    val comparator = DirectionComparator(direction)
    map.filterValues { it == 'O' }
        .toList()
        .sortedWith(comparator)
        .forEach { (point, _) ->
            // add direction
            when (direction) {
                Direction.North -> {
                    (point.y - 1 downTo 0)
                        .takeWhile { map[Point(point.x, it)] == '.' }
                        .lastOrNull()
                        ?.let { newY ->
                            map[point] = '.'
                            map[Point(point.x, newY)] = 'O'
                        }
                }

                Direction.East -> {
                    (point.x + 1..<width)
                        .takeWhile { map[Point(it, point.y)] == '.' }
                        .lastOrNull()
                        ?.let { newX ->
                            map[point] = '.'
                            map[Point(newX, point.y)] = 'O'
                        }
                }

                Direction.South -> {
                    (point.y + 1..<height)
                        .takeWhile { map[Point(point.x, it)] == '.' }
                        .lastOrNull()
                        ?.let { newY ->
                            map[point] = '.'
                            map[Point(point.x, newY)] = 'O'
                        }
                }

                Direction.West -> {
                    (point.x - 1 downTo 0)
                        .takeWhile { map[Point(it, point.y)] == '.' }
                        .lastOrNull()
                        ?.let { newX ->
                            map[point] = '.'
                            map[Point(newX, point.y)] = 'O'
                        }
                }
            }
        }

    return map.filterValues { it == 'O' }.entries.toList()
        .sumOf { (point, _) -> height - point.y }
}

private fun Map<Point, Char>.print() {
    val minMaxX: IntRange = minOf { it.key.x }..maxOf { it.key.x }
    val minMaxY: IntRange = minOf { it.key.y }..maxOf { it.key.y }

    for (y in minMaxY) {
        for (x in minMaxX) {
            print(this[Point(x, y)])
        }
        println()
    }
    println()
}

private fun findRepeating(states: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
    val regex = """(.+?)\1+$""".toRegex()
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