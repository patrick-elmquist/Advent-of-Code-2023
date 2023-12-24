package day01

import common.Input
import common.day

// answer #1: 55834
// answer #2: 53221

fun main() {
    day(n = 1) {
        part1 { input ->
            solve1(input)
        }
        verify {
            expect result 55834
            run test 1 expect 142
        }

        part2 { input ->
            solve2(input)
        }
        verify {
            expect result 53221
            run test 2 expect 281
        }
    }
}

fun solve1(input: Input) = input.lines.sumOf { line ->
    line.mapNotNull(Char::digitToIntOrNull).let { it.first() * 10 + it.last() }
}

fun solve2(input: Input): Int {
    val numberMap = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
        .flatMapIndexed { i, number -> listOf((i + 1).toString() to i + 1, number to i + 1) }
        .toMap()

    return input.lines.sumOf { line ->
        val first = numberMap.minBy { (it, _) -> line.indexOfOrNull(it) ?: Int.MAX_VALUE }
        val last = numberMap.maxBy { (it, _) -> line.lastIndexOfOrNull(it) ?: Int.MIN_VALUE }
        first.value * 10 + last.value
    }
}

private fun String.indexOfOrNull(item: String) = indexOf(item).takeIf { it >= 0 }
private fun String.lastIndexOfOrNull(item: String) = lastIndexOf(item).takeIf { it >= 0 }
