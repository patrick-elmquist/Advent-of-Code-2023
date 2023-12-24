package day01

import common.Input
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test

@DisplayName("Day 1")
class Day01Test {
    @Nested
    @DisplayName("Part 1")
    inner class Part1 {
        @Test
        fun `Example 1`() {
            assertEquals(142, solve1(test(1)))
        }

        @Test
        fun `Real input`() {
            assertEquals(55_834, solve1(actual()))
        }
    }

    @Nested
    @DisplayName("Part 2")
    inner class Part2 {
        @Test
        fun `Example 2`() {
            assertEquals(281, solve2(test(2)))
        }

        @Test
        fun `Real input`() {
            assertEquals(53_221, solve2(actual()))
        }
    }
}

private fun test(n: Int) = Input(test = n, day = 1)
private fun actual() = Input(day = 1)
