package day12

import common.Input
import common.day

// answer #1: 8180
// answer #2: 620189727003627

fun main() {
    day(n = 12) {
        part1 { input ->
            parseInput(input)
                .sumOf { (springs, groups) ->
                    countCombinations(
                        springs = springs,
                        groups = groups
                    )
                }
        }
        verify {
            expect result 8180L
            run test 1 expect 21L
        }

        part2 { input ->
            parseInput(input)
                .sumOf { (springs, group) ->
                    countCombinations(
                        springs = repeat5Times { springs }.joinToString("?"),
                        groups = repeat5Times { group }.flatten(),
                    )
                }
        }
        verify {
            expect result 620189727003627L
            run test 1 expect 525152L
        }
    }
}

private fun countCombinations(
    springs: String,
    groups: List<Int>,
    memo: MutableMap<Pair<String, List<Int>>, Long> = hashMapOf()
): Long {
    if (groups.isEmpty()) {
        return if (springs.none { it == '#' }) 1 else 0
    }

    if (springs.isEmpty()) {
        return 0
    }

    return memo.getOrPut(springs to groups) {
        val current = springs.first()
        val amount = groups.first()

        var total = 0L
        if (current in ".?") {
            total += countCombinations(
                springs = springs.drop(1),
                groups = groups,
                memo = memo,
            )
        }

        if (current in "#?" && amount <= springs.length) {
            val hasNoDots = springs.take(amount).none { it == '.' }
            val hasProperEnding = amount == springs.length || springs[amount] in ".?"

            if (hasNoDots && hasProperEnding) {
                total += countCombinations(
                    springs = springs.drop(amount + 1),
                    groups = groups.drop(1),
                    memo = memo,
                )
            }
        }

        total
    }
}

private fun <T> repeat5Times(block: () -> T): List<T> = generateSequence(block).take(5).toList()

private fun parseInput(input: Input): List<Pair<String, List<Int>>> {
    return input.lines.map {line ->
        line.split(" ").let { (a, b) -> a to b.split(",").map(String::toInt) }
    }
}
