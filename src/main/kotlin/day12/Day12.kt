package day12

import common.day

// answer #1: 8180
// answer #2: 620189727003627

fun main() {
    day(n = 12) {
        part1 { input ->
            input.lines.sumOf { line ->
                val (pattern, counts) = line.split(" ")
                    .let { (a, b) -> a to b.split(",").map(String::toInt) }
                countArrangementsImproved(pattern, amounts = counts)
            }
        }
        verify {
            expect result 8180L
            run test 1 expect 21L
        }

        part2 { input ->
            input.lines.sumOf { line ->
                val (pattern, counts) = line.split(" ")
                    .let { (a, b) -> a to b.split(",").map(String::toInt) }

                val patternExpanded = generateSequence { pattern }
                    .take(n = 5).joinToString("?")
                val countsExpanded = generateSequence { counts }
                    .take(n = 5).toList().flatten()

                countArrangementsImproved(patternExpanded, countsExpanded)
            }
        }
        verify {
            expect result 620189727003627L
            run test 1 expect 525152L
        }
    }
}

private fun countArrangementsImproved(
    input: String,
    amounts: List<Int>,
    memo: MutableMap<Pair<String, List<Int>>, Long> = hashMapOf()
): Long {
    if (amounts.isEmpty()) {
        return if ('#' in input) 0 else 1
    }

    if (input.isEmpty()) {
        return 0
    }

    return memo.getOrPut(input to amounts) {
        var total = 0L
        val current = input.first()
        val amount = amounts.first()

        if (current in ".?") {
            total += countArrangementsImproved(
                input = input.drop(1),
                amounts = amounts,
                memo = memo,
            )
        }

        val hasEnoughSpaceLeft = amount <= input.length
        if (current in "#?" && hasEnoughSpaceLeft) {
            val hasNoSpacing = '.' !in input.take(amount)
            val hasProperEnding = amount == input.length || input[amount] in ".?"
            if (hasNoSpacing && hasProperEnding) {
                total += countArrangementsImproved(
                    input = input.drop(amount + 1),
                    amounts = amounts.drop(1),
                    memo = memo,
                )
            }
        }

        total
    }
}
