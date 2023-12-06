package day06

import common.day
import common.util.log

// answer #1: 449820
// answer #2:

fun main() {
    day(n = 6) {
        part1 { input ->
            val pattern = """(\d+)""".toRegex()
            val (times, distances) = input.lines
                .map { pattern.findAll(it).map(MatchResult::value).map(String::toInt).toList() }

            times.log()
            distances.log()

            times.zip(distances).map { (time, distance) ->
                var hold = 0
                var possible = 0
                while (hold < time) {
                    if (hold * (time - hold) > distance) {
                        possible++
                    }
                    hold++
                }
                possible
            }.reduce(Int::times)
        }
        verify {
            expect result 449820
            run test 1 expect 288
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}