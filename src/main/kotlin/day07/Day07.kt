package day07

import common.day

// answer #1: 251121738
// answer #2: 251421071

private const val JOKER = 1

private const val FIVE_OF_A_KIND = 6
private const val FOUR_OF_A_KIND = 5
private const val FULL_HOUSE = 4
private const val THREE_OF_A_KIND = 3
private const val TWO_PAIR = 2
private const val ONE_PAIR = 1
private const val HIGH_CARD = 0

fun main() {
    day(n = 7) {
        part1 { input ->
            calculateWinnings(input.lines, useJokers = false)
        }
        verify {
            expect result 251121738L
            run test 1 expect 6440L
        }

        part2 { input ->
            calculateWinnings(input.lines, useJokers = true)
        }
        verify {
            expect result 251421071L
            run test 1 expect 5905L
        }
    }
}

private fun calculateWinnings(lines: List<String>, useJokers: Boolean): Long =
    lines.asSequence()
        .map { line -> parseCards(line, withJokers = useJokers) }
        .sortedWith { o1, o2 -> compareHands(o1.first, o2.first, useJokers = useJokers) }
        .map(Pair<List<Int>, Long>::second)
        .withIndex()
        .sumOf { (index, bid) -> (index + 1) * bid }

private fun parseCards(line: String, withJokers: Boolean): Pair<List<Int>, Long> =
    line.split(" ")
        .let { (hand, bid) ->
            hand.map { card -> cardToInt(card, withJokers) } to bid.toLong()
        }

private fun cardToInt(it: Char, withJokers: Boolean) =
    when (it) {
        in '0'..'9' -> it.digitToInt()
        'T' -> 10
        'J' -> if (withJokers) JOKER else 11
        'Q' -> 12
        'K' -> 13
        'A' -> 14
        else -> error("")
    }

private fun getType(cards: List<Int>, useJokers: Boolean): Int {
    val cardSet = cards.groupingBy { it }.eachCount()
    val countOfMostCommon = cardSet.maxOf { it.value }

    val type = when (cardSet.size) {
        1 -> FIVE_OF_A_KIND
        2 -> if (countOfMostCommon == 4) FOUR_OF_A_KIND else FULL_HOUSE
        3 -> if (countOfMostCommon == 3) THREE_OF_A_KIND else TWO_PAIR
        4 -> ONE_PAIR
        else -> HIGH_CARD
    }

    return if (useJokers) {
        applyJokers(type, cards.count { it == JOKER })
    } else {
        type
    }
}

private fun applyJokers(type: Int, jokers: Int): Int {
    if (jokers <= 0) return type
    return when (type) {
        FIVE_OF_A_KIND -> FIVE_OF_A_KIND
        FOUR_OF_A_KIND -> FIVE_OF_A_KIND
        FULL_HOUSE -> FIVE_OF_A_KIND
        THREE_OF_A_KIND -> FOUR_OF_A_KIND
        TWO_PAIR -> {
            if (jokers == 2) {
                FOUR_OF_A_KIND
            } else {
                FULL_HOUSE
            }
        }
        ONE_PAIR -> THREE_OF_A_KIND
        HIGH_CARD -> ONE_PAIR
        else -> error("type:$type jokers:$jokers")
    }
}

private fun compareHands(a: List<Int>, b: List<Int>, useJokers: Boolean = false): Int {
    val typeA = getType(a, useJokers)
    val typeB = getType(b, useJokers)

    return if (typeA != typeB) {
        typeA.compareTo(typeB)
    } else {
        a.zip(b).firstOrNull { (a1, b1) -> a1 != b1 }?.let { (a1, b1) -> a1.compareTo(b1) } ?: 0
    }
}
