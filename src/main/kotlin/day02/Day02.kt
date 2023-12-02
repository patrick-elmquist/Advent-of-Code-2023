package day02

import common.day
import common.util.log
import kotlin.math.exp
import kotlin.math.max

// answer #1: 2541
// answer #2: 66016

fun main() {
    day(n = 2) {
        part1(expect = 2541) { input ->
            val available = mapOf("red" to 12, "green" to 13, "blue" to 14)

            input.lines.sumOf { line ->
                var s = line.drop(5)
                val id = s.takeWhile { it != ':' }.toInt()
                s = s.dropWhile { it != ':' }.drop(2)
                val split = s.split(";").map { it.trim() }
                val result = split.all { input ->
                    val s1 = input.split(", ")
                    s1.all {
                        val (count, color) = it.split(" ")
                        count.toInt() <= available.getValue(color)
                    }
                }
                if (result) id else 0
            }
        }
        part1 test 1 expect 8

        part2(expect = 66016) { input ->
            input.lines.sumOf { line ->
                val required = mutableMapOf("red" to 0, "green" to 0, "blue" to 0)
                var s = line.drop(5)
                s = s.dropWhile { it != ':' }.drop(2)
                val split = s.split(";").map { it.trim() }
                split.forEach { input ->
                    val s1 = input.split(", ")
                    s1.forEach {
                        val (count, color) = it.split(" ")
                        required[color] = max(count.toInt(), required.getValue(color))
                    }
                }
                required.values.reduce { acc, i -> acc * i }
            }
        }
        part2 test 1 expect 2286
    }
}