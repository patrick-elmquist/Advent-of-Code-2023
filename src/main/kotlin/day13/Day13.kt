package day13

import common.day
import common.util.Point
import common.util.sliceByBlank
import kotlin.math.min

// answer #1: 35538
// answer #2: 30442

fun main() {
    day(n = 13) {
        part1 { input ->
            input.lines.sliceByBlank().fold(0) { acc, pattern ->
                val (cols, rows) = parseColsAndRows(pattern)
                val (l, a) = findReflectionDistance(cols, rows)
                acc + l + a * 100
            }
        }
        verify {
            expect result 35538
            run test 1 expect 405
        }

        part2 { input ->
            input.lines.sliceByBlank().fold(0) { acc, pattern ->
                val (cols, rows) = parseColsAndRows(pattern)
                val point = findDiffPoint(cols, rows)
                val (repairedCols, repairedRows) = pattern
                    .mapIndexed { y, row ->
                        if (y == point.y) {
                            row.toMutableList()
                                .apply { set(point.x, if (get(point.x) == '#') '.' else '#') }
                                .joinToString("")
                        } else {
                            row
                        }
                    }
                    .let { parseColsAndRows(it) }

                val (unmodifiedLeft, unmodifiedAbove) = findReflectionDistance(cols, rows)
                val (l, a) = findReflectionDistance(
                    cols = repairedCols,
                    rows = repairedRows,
                    ignoreLeft = unmodifiedLeft,
                    ignoreAbove = unmodifiedAbove
                )
                acc + l + a * 100
            }
        }
        verify {
            expect result 30442
            run test 1 expect 400
        }
    }
}

private fun findReflectionDistance(
    cols: List<String>,
    rows: List<String>,
    ignoreLeft: Int? = null,
    ignoreAbove: Int? = null,
): Pair<Int, Int> {
    for (i in 1..cols.lastIndex) {
        if (isPerfectMirrorAtIndex(cols, i) && i != ignoreLeft) return i to 0
    }

    for (i in 1..rows.lastIndex) {
        if (isPerfectMirrorAtIndex(rows, i) && i != ignoreAbove) return 0 to i
    }

    error("could not find diff")
}

private fun isPerfectMirrorAtIndex(pattern: List<String>, index: Int): Boolean {
    val (left, right) = pattern.withIndex().partition { (i, _) -> i < index }

    val count = left.reversed().zip(right)
        .takeWhile { (l, r) -> l.value == r.value }
        .count()

    return count == min(left.size, right.size)
}

private fun findDiffPoint(
    cols: List<String>,
    rows: List<String>,
) = listOf(cols, rows).firstNotNullOf { range ->
    (1..range.lastIndex).firstNotNullOfOrNull { i ->
        findDiffPoint(
            index = i,
            isRow = range == rows,
            input = calculateFoldDiffs(range, i)
        )
    }
}

private fun calculateFoldDiffs(strings: List<String>, index: Int): List<List<Int>> {
    val (left, right) = strings.withIndex().partition { (i, _) -> i < index }
    return left.reversed().zip(right)
        .map { (l, r) -> l.value.zip(r.value).map { (a, b) -> if (a == b) 0 else 1 } }
}

private fun findDiffPoint(index: Int, isRow: Boolean, input: List<List<Int>>): Point? {
    if (input.sumOf { it.sum() } != 1) return null

    val x = input.first { it.sum() == 1 }.indexOfFirst { it == 1 }
    val y = index - 1 - input.indexOfFirst { it.sum() == 1 }
    return if (isRow) Point(x, y) else Point(y, x)
}

private fun parseColsAndRows(matrix: List<String>): Pair<List<String>, List<String>> {
    val cols = (0..<matrix.first().length)
        .map { c -> matrix.map { it[c] }.joinToString("") }
    return cols to matrix
}
