package day02

import common.Input
import common.day

// answer #1: 2541
// answer #2: 66016

fun main() {

    val entryPattern = """(\d+) (\w+)""".toRegex()
    fun parseMapsOfMaxValues(input: Input) = input.lines.map {
        entryPattern.findAll(it)
            .map(MatchResult::destructured)
            .groupBy { (_, color) -> color }
            .mapValues { (_, vals) -> vals.maxOf { (count) -> count.toInt() } }
    }

    day(n = 2) {
        part1(expect = 2541) { input ->
            val available = mapOf("red" to 12, "green" to 13, "blue" to 14)
            val mapsOfMaxValues = parseMapsOfMaxValues(input)
            mapsOfMaxValues.withIndex()
                .filter { (_, map) -> map.all { (color, count) -> count <= available.getValue(color) } }
                .sumOf { (index, _) -> index + 1 }
        }
        part1 test 1 expect 8

        part2(expect = 66016) { input ->
            val mapsOfMaxValues = parseMapsOfMaxValues(input)
            mapsOfMaxValues.sumOf { it.values.reduce(Int::times) }
        }
        part2 test 1 expect 2286
    }
}