package day06

import common.Input
import common.day

// answer #1: 449820
// answer #2: 42250895

fun main() {
    day(n = 6) {
        part1 { input ->
            val (times, distances) = mapLinesToNumbers(input)
                .map { it.map(String::toLong) }

            times.zip(distances)
                .map { (time, distance) -> countPossibleHoldTimes(time, distance) }
                .reduce(Long::times)
        }
        verify {
            expect result 449820L
            run test 1 expect 288L
        }

        part2 { input ->
            val (time, distance) = mapLinesToNumbers(input)
                .map { it.joinToString("") }
                .map(String::toLong)

            countPossibleHoldTimes(time, distance)
        }
        verify {
            expect result 42250895L
            run test 1 expect 71503L
        }
    }
}

private val pattern = """(\d+)""".toRegex()
private fun mapLinesToNumbers(input: Input) =
    input.lines.map { pattern.findAll(it).map(MatchResult::value) }

private fun countPossibleHoldTimes(time: Long, distance: Long): Long {
    var hold = 0L
    var possible = 0L
    while (hold < time) {
        if (hold * (time - hold) > distance) possible++
        hold++
    }
    return possible
}