package day01

import common.day

// answer #1: 55834
// answer #2: 53221

fun main() {
    day(n = 1) {
        part1(expected = 55834) { input ->
            input.lines.sumOf { line ->
                line.mapNotNull { it.digitToIntOrNull() }.let { it.first() * 10 + it.last() }
            }
        }
        part1 test 1 expect 142

        part2(expected = 53221) { input ->
            val numberMap = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
                .flatMapIndexed { i, number -> listOf((i + 1).toString() to i + 1, number to i + 1) }.toMap()

            input.lines.sumOf { line ->
                val first = numberMap.minBy { (it, _) -> line.indexOf(it).takeIf { it >= 0 } ?: Int.MAX_VALUE }
                val last = numberMap.maxBy { (it, _) -> line.lastIndexOf(it).takeIf { it >= 0 } ?: Int.MIN_VALUE }
                first.value * 10 + last.value
            }
        }
        part2 test 2 expect 281
    }
}
