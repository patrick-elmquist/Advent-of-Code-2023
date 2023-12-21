package day21

import common.Input
import common.bounds
import common.day
import common.util.*
import java.util.*
import kotlin.math.log

// answer #1: 3572
// answer #2:

fun main() {
    day(n = 21) {
        part1 { input ->
            val steps = 64
            val map = input.lines.drop(1).pointCharMap
            val start = map.entries.first { it.value == 'S' }.key
            val distances = calculateDistances(start, steps, map)

            distances.entries
                .filter { it.value % 2 == 0 }
                .groupingBy { it.value }
                .eachCount()
                .values
                .sum()
        }
        verify {
            expect result 3572
        }

        part2 {

        }
        verify {
            expect result null
//            run test 1 expect Unit
        }
    }
}

private fun solve(input: Input, steps: Int): Long {
    val map = input.lines.drop(1).pointCharMap
    val start = map.entries.first { it.value == 'S' }.key

    val distances = calculateDistances(start, steps, map)

    return distances.entries
        .groupingBy { it.value }
        .eachCount()
        .values
        .also {
            it.zipWithNext { a, b -> b - a}.log()
        }
        .log()
        .sumOf { it.toLong() }
}

private fun calculateDistances2(
    start: Point,
    steps: Int,
    map: Map<Point, Char>
): MutableMap<Point, Int> {
    val width = map.maxOf { it.key.x } + 1
    val height = map.maxOf { it.key.y } + 1
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val visited = mutableSetOf<Point>()

    val queue = PriorityQueue<Pair<Point, Int>> { o1, o2 ->
        distances.getValue(o1.first).compareTo(distances.getValue(o2.first))
    }
    queue.add(start to steps)

    val minMaxX = map.minMax { it.key.x }
    val minMaxY = map.minMax { it.key.y }
    for (y in minMaxY) {
        for (x in minMaxX) {
            print(map[Point(x, y)])
        }
        println()
    }
    println()
    while (queue.isNotEmpty()) {
        val (point, stepsLeft) = queue.poll()

        visited += point

        val nextStepsLeft = stepsLeft - 1
        if (nextStepsLeft >= 0) {
            point.neighbors()
                .filter { it !in visited }
                .filter { p ->
                    val lookupPoint = lookupPoint(p, width, height)
                    map[lookupPoint] in setOf('.', 'S')
                }
                .forEach { next ->
                    val dist = distances.getValue(next)
                    val newDist = distances.getValue(point) + 1
                    if (newDist < dist) {
                        distances[next] = newDist
                        queue += next to nextStepsLeft
                    }
                }
        }
    }

    val minMaxX2 = distances.minMax { it.key.x }
    val minMaxY2 = distances.minMax { it.key.y }
    for (y in minMaxY2) {
        for (x in minMaxX2) {
            val p = Point(x, y)
            if (p == start) {
                print('S')
            } else if (p in distances) {
//                print(distances[p])
                print('*')
            } else {
                val message = map[lookupPoint(p, width, height)]
                if (message == 'S') {
                    print('.')
                } else {
                    print(message)
                }
            }
        }
        println()
    }
    println()
    return distances
}

private fun lookupPoint(
    point: Point,
    width: Int,
    height: Int,
): Point {
    val (x, y) = point

    val newX = if (x < 0) {
        (width + (x % width)) % width
    } else {
        x % width
    }
    val newY = if (y < 0) {
        (height + (y % height)) % height
    } else {
        y % height
    }
    return Point(newX, newY)
}

private fun calculateDistances(
    start: Point,
    steps: Int,
    map: Map<Point, Char>
): MutableMap<Point, Int> {
    val distances = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    distances[start] = 0

    val visited = mutableSetOf<Point>()

    val queue = PriorityQueue<Pair<Point, Int>> { o1, o2 ->
        distances.getValue(o1.first).compareTo(distances.getValue(o2.first))
    }
    queue.add(start to steps)

    while (queue.isNotEmpty()) {
        val (point, stepsLeft) = queue.poll()

        visited += point

        val nextStepsLeft = stepsLeft - 1
        if (nextStepsLeft >= 0) {
            point.neighbors()
                .filter { it !in visited }
                .filter { map[it] == '.' }
                .forEach { next ->
                    val dist = distances.getValue(next)
                    val newDist = distances.getValue(point) + 1
                    if (newDist < dist) {
                        distances[next] = newDist
                        queue += next to nextStepsLeft
                    }
                }
        }
    }
    return distances
}