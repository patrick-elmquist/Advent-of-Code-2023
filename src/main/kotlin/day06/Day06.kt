package day06

import common.day

// answer #1: 449820
// answer #2: 42250895

fun main() {
    day(n = 6) {
        val pattern = """(\d+)""".toRegex()

        part1 { input ->
            val (times, distances) = input.lines
                .map { pattern.findAll(it).map(MatchResult::value).map(String::toLong).toList() }

            times.zip(distances).map { (time, distance) -> countPossibleHoldTimes(time, distance) }
                .reduce(Long::times)
        }
        verify {
            expect result 449820L
            run test 1 expect 288L
        }

        part2 { input ->
            val (time, distance) = input.lines
                .map { pattern.findAll(it).map(MatchResult::value).joinToString("") }
                .map(String::toLong)

            countPossibleHoldTimes(time, distance)
        }
        verify {
            expect result 42250895L
            run test 1 expect 71503L
        }
    }
}

private fun countPossibleHoldTimes(time: Long, distance: Long): Long {
    var hold = 0L
    var possible = 0L
    while (hold < time) {
        if (hold * (time - hold) > distance) possible++
        hold++
    }
    return possible
}