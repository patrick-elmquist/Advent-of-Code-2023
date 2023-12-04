package day02

import common.Input
import common.day

// answer #1: 2541
// answer #2: 66016

fun main() {
    day(n = 2) {
        part1 { input ->
            val available = mapOf("red" to 12, "green" to 13, "blue" to 14)
            val mapsOfMaxValues = parseMapsOfMaxValues(input)
            mapsOfMaxValues.withIndex()
                .filter { (_, map) -> map.all { (color, count) -> count <= available.getValue(color) } }
                .sumOf { (index, _) -> index + 1 }
        }
        verify {
            expect result 2541
            run test 1 expect 8
        }

        part2 { input ->
            val mapsOfMaxValues = parseMapsOfMaxValues(input)
            mapsOfMaxValues.sumOf { it.values.reduce(Int::times) }
        }
        verify {
            expect result 66016
            run test 1 expect 2286
        }
    }
}

private val pattern = """(\d+) (\w+)""".toRegex()
private fun parseMapsOfMaxValues(input: Input) = input.lines.map { line ->
    pattern.findAll(line)
        .map(MatchResult::destructured)
        .groupBy { (_, color) -> color }
        .mapValues { (_, value) -> value.maxOf { (count) -> count.toInt() } }
}
