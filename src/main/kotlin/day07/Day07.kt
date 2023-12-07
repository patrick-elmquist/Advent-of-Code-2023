package day07

import common.day
import common.util.log

// answer #1: 251121738
// answer #2:

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
            input.lines.map { parseLine(it) }
                .sortedWith { o1, o2 -> compareHands(o1.first, o2.first) }
                .reversed()
                .withIndex()
                .sumOf { (index, pair) -> (index + 1) * pair.second }
        }
        verify {
            expect result null
            run test 1 expect 5905
        }
    }
}

private fun parseLine(it: String): Pair<List<Int>, Long> {
    return it.split(" ").let { (hand, bid) ->
        hand.map { charToInt(it) } to bid.toLong()
    }
}

private fun intToChar(it: Int) = if (it in 0..9) {
    it
} else {
    when (it) {
        10 -> 'T'
        11 -> 'J'
        12 -> 'Q'
        13 -> 'K'
        14 -> 'A'
        else -> error("")
    }
}

private fun charToInt(it: Char) = if (it.isDigit()) {
    it.digitToInt()
} else {
    when (it) {
        'T' -> 10
        'J' -> 11
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> error("")
    }
}

private fun getType(card: List<Int>): Int {
    val charSet = card.groupingBy { it }.eachCount()
    val max = charSet.maxBy { it.value }
    return when (charSet.size) {
        1 -> 0 // five
        2 -> {
            if (max.value == 4) {
                1 // four
            } else {
                2 // full
            }
        }

        3 -> {
            if (max.value == 3) {
                3 //three
            } else {
                4 // two pairs
            }
        }

        4 -> {
            5 // one pair
        }

        else -> 6 // high card
    }
}

private fun compareHands(a: List<Int>, b: List<Int>): Int {
    val typeA = getType(a)
    val typeB = getType(b)

    return if (typeA != typeB) {
        typeA.compareTo(typeB)
    } else {
        a.zip(b).firstOrNull { (a1, b1) -> a1 != b1 }?.let { (a1, b1) ->
            b1.compareTo(a1)
        } ?: 0
    }
}