package day04

import common.Input
import common.day
import kotlin.math.pow

// answer #1: 28750
// answer #2: 10212704

fun main() {
    day(n = 4) {
        part1 { input ->
            parseWinningNumbers(input)
                .filter { it.isNotEmpty() }
                .sumOf { 2f.pow(it.size - 1).toInt() }
        }
        verify {
            expect result 28750
            run test 1 expect 13
        }

        part2 { input ->
            parseWinningNumbers(input)
                .withIndex()
                .filter { (_, winners) -> winners.isNotEmpty() }
                .fold(IntArray(input.lines.size) { 1 }) { counts, (index, winners) ->
                    (index + 1..index + winners.size).forEach { i ->
                        if (i in counts.indices) {
                            counts[i] += counts[index]
                        }
                    }
                    counts
                }.sum()
        }
        verify {
            expect result 10212704
            run test 1 expect 30
        }
    }
}

private val regex = """\s+""".toRegex()
private fun parseWinningNumbers(input: Input): List<Set<Int>> =
    input.lines.map { line ->
        val (winning, has) = line
            .split(":")[1].split("|")
            .map { it.trim().split(regex).map(String::toInt).toSet() }
        has.intersect(winning)
    }
