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
            parts.filter { part -> part.surrounding.any { symbols.containsKey(it) } }.sumOf { it.value }
        }
        part1 test 1 expect 4361

        part2(expect = 79842967) { input ->
            val (parts, symbols) = parsePartsAndSymbols(input)
            symbols.filter { (_, symbol) -> symbol == '*' }
                .keys
                .sumOf { point ->
                    val valid = parts.filter { point in it.surrounding }
                    if (valid.size == 2) valid.map { it.value }.reduce(Int::times) else 0
                }
        }
        part2 test 1 expect 467835
    }
}

private fun parsePartsAndSymbols(input: Input): Pair<List<Part>, MutableMap<Point, Char>> {
    val pattern = """(\d+)""".toRegex()
    val symbols = mutableMapOf<Point, Char>()
    val parts = input.lines.flatMapIndexed { y, line ->
        line.withIndex().filter { (_, c) -> !c.isDigit() && c != '.' }
            .forEach { (x, symbol) -> symbols[Point(x, y)] = symbol }
        pattern.findAll(line).map {
            Part(it.value, Point(it.range.first, y))
        }
    }
    return parts to symbols
}

private data class Part(val number: String, val point: Point) {
    val value = number.toInt()
    val surrounding = buildSet {
        add(point.copy(x = point.x - 1))
        add(point.copy(x = point.x + number.length))
        for (x in (point.x - 1)..(point.x + number.length)) {
            add(Point(x, point.y - 1))
            add(Point(x, point.y + 1))
        }
    }
}
