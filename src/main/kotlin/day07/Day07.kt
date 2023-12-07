package day07

import ch.qos.logback.core.subst.Token.Type
import common.day
import common.util.log

// answer #1: 251121738
// answer #2: 251421071

fun main() {
    day(n = 7) {
        part1 { input ->
            input.lines.map { parseLine(it) }
                .sortedWith { o1, o2 -> compareHands(o1.first, o2.first) }
                .reversed()
                .withIndex()
                .sumOf { (index, pair) -> (index + 1) * pair.second }
        }
        verify {
            expect result 251121738L
            run test 1 expect 6440L
        }

        part2 { input ->
            input.lines.map { parseLine(it, withJokers = true) }
                .sortedWith { o1, o2 -> compareHands(o1.first, o2.first, elevate = true) }
                .reversed()
                .withIndex()
                .sumOf { (index, pair) -> (index + 1) * pair.second }
        }
        verify {
            expect result 251421071L
            run test 1 expect 5905L
        }
    }
}

private const val FIVE = 0
private const val FOUR = 1
private const val FULL_HOUSE = 2
private const val THREE = 3
private const val TWO_PAIRS = 4
private const val ONE_PAIR = 5
private const val HIGH = 6

private fun elevateType(type: Int, jokers: Int): Int {
    if (jokers <= 0) return type
    return when (type) {
        FIVE -> FIVE
        FOUR -> FIVE
        FULL_HOUSE -> FIVE
        THREE -> FOUR
        TWO_PAIRS -> {
            if (jokers == 2) {
                FOUR
            } else {
                FULL_HOUSE
            }
        }

        ONE_PAIR -> THREE
        HIGH -> ONE_PAIR
        else -> error("type:$type jokers:$jokers")
    }
}

private fun getType(card: List<Int>): Int {
    val charSet = card.groupingBy { it }.eachCount()
    val max = charSet.maxBy { it.value }
    return when (charSet.size) {
        1 -> FIVE // five
        2 -> {
            if (max.value == 4) {
                FOUR // four
            } else {
                FULL_HOUSE // full
            }
        }

        3 -> {
            if (max.value == 3) {
                THREE //three
            } else {
                TWO_PAIRS // two pairs
            }
        }

        4 -> {
            ONE_PAIR // one pair
        }

        else -> HIGH // high card
    }
}

private fun compareHands(a: List<Int>, b: List<Int>, elevate: Boolean = false): Int {
    val typeA = if (elevate) {
        val jokersA = a.count { it == 1 }
        elevateType(getType(a), jokersA)
    } else {
        getType(a)
    }

    val typeB = if (elevate) {
        val jokersB = b.count { it == 1 }
        elevateType(getType(b), jokersB)
    } else {
        getType(b)
    }

    return if (typeA != typeB) {
        typeA.compareTo(typeB)
    } else {
        a.zip(b).firstOrNull { (a1, b1) -> a1 != b1 }?.let { (a1, b1) ->
            b1.compareTo(a1)
        } ?: 0
    }
}

private fun parseLine(it: String, withJokers: Boolean = false): Pair<List<Int>, Long> =
    it.split(" ").let { (hand, bid) -> hand.map { charToInt(it, withJokers) } to bid.toLong() }

private fun intToChar(it: Int) = if (it in 0..9) {
    it
} else {
    when (it) {
        1 -> 'J'
        10 -> 'T'
        11 -> 'J'
        12 -> 'Q'
        13 -> 'K'
        14 -> 'A'
        else -> error("")
    }
}

private fun charToInt(it: Char, withJokers: Boolean = false) = if (it.isDigit()) {
    it.digitToInt()
} else {
    when (it) {
        'T' -> 10
        'J' -> if (withJokers) 1 else 11
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> error("")
    }
}

