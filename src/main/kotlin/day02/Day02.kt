package day02

import common.day
import common.util.match
import kotlin.math.max

// answer #1: 2541
// answer #2: 66016

fun main() {

    val pattern = """Game (\d+): (.*)""".toRegex()
    fun parseIdAndRounds(line: String): Pair<Int, List<List<List<String>>>> {
        return pattern.match(line) { (id, rounds) ->
            id.toInt() to rounds.split("; ").map { it.split(", ").map { it.split(" ") } }
        }
    }

    day(n = 2) {
        part1(expect = 2541) { input ->
            val available = mapOf("red" to 12, "green" to 13, "blue" to 14)
            input.lines.sumOf { line ->
                val (id, rounds) = parseIdAndRounds(line)
                val areAllValid = rounds.all { round ->
                    round.all { (count, color) -> count.toInt() <= available.getValue(color) }
                }
                id.takeIf { areAllValid } ?: 0
            }
        }
        part1 test 1 expect 8

        part2(expect = 66016) { input ->
            input.lines.sumOf { line ->
                val (_, rounds) = parseIdAndRounds(line)
                val required = mutableMapOf<String, Int>()
                rounds.forEach { round ->
                    round.forEach { (count, color) ->
                        required.merge(color, count.toInt(), ::max)
                    }
                }
                required.values.reduce(Int::times)
            }
        }
        part2 test 1 expect 2286
    }
}