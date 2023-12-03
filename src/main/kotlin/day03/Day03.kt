package day03

import common.Input
import common.day
import common.util.Point

// answer #1: 532445
// answer #2: 79842967

fun main() {
    day(n = 3) {
        part1(expect = 532445) { input ->
            val (parts, symbols) = parsePartsAndSymbols(input)
            parts.filter { part -> part.neighbors.any { symbols.containsKey(it) } }
                .sumOf(Part::value)
        }
        part1 test 1 expect 4361

        part2(expect = 79842967) { input ->
            val (parts, symbols) = parsePartsAndSymbols(input)
            symbols.filter { (_, symbol) -> symbol == '*' }
                .keys
                .map { position -> parts.filter { part -> position in part.neighbors }.map(Part::value) }
                .filter { partNumbers -> partNumbers.size == 2 }
                .sumOf { partNumbers -> partNumbers.reduce(Int::times) }
        }
        part2 test 1 expect 467835
    }
}

private fun parsePartsAndSymbols(input: Input): Pair<List<Part>, MutableMap<Point, Char>> {
    val pattern = """(\d+)""".toRegex()
    val parts = mutableListOf<Part>()
    val symbols = mutableMapOf<Point, Char>()
    input.lines.forEachIndexed { y, line ->
        symbols += line.withIndex()
            .filter { (_, c) -> !c.isDigit() && c != '.' }
            .map { (x, symbol) -> Point(x, y) to symbol }

        parts += pattern.findAll(line).map { Part(it.value, Point(it.range.first, y)) }
    }
    return parts to symbols
}

private data class Part(private val number: String, private val point: Point) {
    val value = number.toInt()
    val neighbors = buildSet {
        add(point.copy(x = point.x - 1))
        add(point.copy(x = point.x + number.length))
        for (x in (point.x - 1)..(point.x + number.length)) {
            add(Point(x, point.y - 1))
            add(Point(x, point.y + 1))
        }
    }
}
