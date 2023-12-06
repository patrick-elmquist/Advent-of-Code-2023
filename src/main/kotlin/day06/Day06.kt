package day06

import common.day

// answer #1: 449820
// answer #2: 42250895

fun main() {
    day(n = 6) {
        val pattern = """(\d+)""".toRegex()

        part1 { input ->
            val (times, distances) = input.lines
                .map { pattern.findAll(it).map(MatchResult::value).map(String::toInt).toList() }

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
            val (time, distance) = input.lines
                .map { pattern.findAll(it).map(MatchResult::value).joinToString("") }
                .map(String::toLong)

            var hold = 0L
            var possible = 0L
            while (hold < time) {
                if (hold * (time - hold) > distance) {
                    possible++
                }
                hold++
            }
            possible
        }
        verify {
            expect result 42250895L
            run test 1 expect 71503L
        }
    }
}