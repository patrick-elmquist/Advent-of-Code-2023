package day15

import common.day

// answer #1: 502139
// answer #2:

fun main() {
    day(n = 15) {
        part1 { input ->
            input.lines.first()
                .split(",")
                .sumOf {
                    it.fold(0L) { current, c ->
                        (current + c.code) * 17 % 256
                    }
                }
        }
        verify {
            expect result 502139
            run test 1 expect 1320L
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}