package day04

import common.day
import kotlin.math.pow

// answer #1: 28750
// answer #2: 10212704

fun main() {
    day(n = 4) {
        val pattern = """(\d+)""".toRegex()
        part1 { input ->
            input.lines.sumOf { line ->
                val split = line.dropWhile { it != ':' }.drop(1).split(" | ")
                val winning = pattern.findAll(split[0]).map { it.value }.toSet()
                val has = pattern.findAll(split[1]).map { it.value }.toSet()
                val winners = has.intersect(winning)
                if (winners.isNotEmpty()) {
                    2f.pow(winners.size - 1).toInt()
                } else {
                    0
                }
            }
        }
        verify {
            expect result 28750
            run test 1 expect 13
        }

        part2 { input ->
            val counts = IntArray(input.lines.size) { 1 }
            input.lines.forEachIndexed { index, line ->
                val split = line.dropWhile { it != ':' }.drop(1).split(" | ")
                val winning = pattern.findAll(split[0]).map { it.value }.toSet()
                val has = pattern.findAll(split[1]).map { it.value }.toSet()
                val winners = has.intersect(winning)
                if (winners.isNotEmpty()) {
                    (index + 1..index + winners.size).forEach { i ->
                        if (i in counts.indices) {
                            counts[i] += counts[index]
                        }
                    }
                }
            }

            counts.sum()
        }
        verify {
            expect result 10212704
            run test 1 expect 30
        }
    }
}