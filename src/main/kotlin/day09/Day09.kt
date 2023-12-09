package day09

import common.day

// answer #1: 1702218515
// answer #2: 925

fun main() {
    day(n = 9) {
        part1 { input ->
            input.lines.sumOf { line ->
                createStepsFromLine(line)
                    .reversed()
                    .map(List<Int>::last)
                    .reduce { acc, n -> acc + n }
            }
        }
        verify {
            expect result 1702218515
            run test 1 expect 114
        }

        part2 { input ->
            input.lines.sumOf { line ->
                createStepsFromLine(line)
                    .reversed()
                    .map(List<Int>::first)
                    .reduce { acc, n -> n - acc }
            }
        }
        verify {
            expect result 925
            run test 1 expect 2
        }
    }
}

private fun createStepsFromLine(line: String): List<List<Int>> {
    val steps = mutableListOf(line.split(" ").map(String::toInt))
    while (steps.last().any { it != 0 }) {
        steps.add(steps.last().windowed(size = 2) { (a, b) -> b - a })
    }
    return steps
}
