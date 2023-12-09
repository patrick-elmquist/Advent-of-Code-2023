package day09

import common.day
import common.util.log

// answer #1: 1702218515
// answer #2:

fun main() {
    day(n = 9) {
        part1 { input ->
            input.lines.sumOf { line ->
                val split = line.split(" ").map(String::toInt)
                val steps = mutableListOf(split)
                while (steps.last().any { it != 0 }) {
                    steps.add(
                        steps.last()
                            .windowed(size = 2, step = 1, partialWindows = false) { (a, b) -> b - a }
                    )
                }
                steps.forEach { it.log() }
                steps.reversed().drop(1).map { it.last() }.fold(0) { acc: Int, i: Int ->
                    acc + i
                }
            }
        }
        verify {
            expect result 1702218515
            run test 1 expect 114
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}