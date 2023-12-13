package day13

import common.day
import common.util.Point
import common.util.log
import common.util.sliceByBlank
import kotlin.math.abs
import kotlin.math.max

// answer #1: 35538
// answer #2: 30442

fun main() {
    day(n = 13) {
        fun parseColsAndRows(matrix: List<String>): Pair<List<String>, List<String>> {
            val cols = (0..<matrix.first().length).map { c ->
                matrix.map { it[c] }.joinToString("")
            }
            return cols to matrix
        }

        fun solve(matrix: List<String>, excludeRow: Int? = null, excludeCol: Int? = null): Pair<Int, Int> {
            val (cols, rows) = parseColsAndRows(matrix)

            var left = 0
            var above = 0
            for (i in 1..cols.lastIndex) {
                val diff = cols.foldAt(i)
                if (diff && i != excludeCol) {
                    left += i
                    break
                }
            }

            for (i in 1..rows.lastIndex) {
                val diff = rows.foldAt(i)
                if (diff && i != excludeRow) {
                    above += i
                    break
                }
            }
            return left to above
        }

        part1 { input ->
            var left = 0
            var above = 0
            input.lines.sliceByBlank().forEach { matrix ->
                val (l, a) = solve(matrix)
                left += l
                above += a
            }
            left + above * 100
        }
        verify {
            expect result 35538
            run test 1 expect 405
        }

        part2 { input ->
            var left = 0
            var above = 0
            input.lines.sliceByBlank().forEachIndexed { index, matrix ->
                val (cols, rows) = parseColsAndRows(matrix)

                var isRow = false
                var diffByOnePoint: Point? = null
                for (i in 1..cols.lastIndex) {
                    val (perfect, diff) = cols.foldAt2(i)
                    val (diffByOne, p) = isDiffByOne(i, isRow = false, input = diff)
                    if (p != null) {
                        diff.log("m$index col $i p:$perfect:")
                        diffByOnePoint = p
                        break
                    }
                }

                for (i in 1..rows.lastIndex) {
                    val (perfect, diff) = rows.foldAt2(i)
                    val (diffByOne, p) = isDiffByOne(i, isRow = true, input = diff)
                    if (p != null) {
                        isRow = true
                        diff.log("m$index row $i p:$perfect:")
                        diffByOnePoint = p
                        break
                    }
                }

                val point = diffByOnePoint!!
                point.log("point")
                val newMatrix = matrix.mapIndexed { y, row ->
                    if (isRow) {
                        if (y == point.y) {
                            row.toMutableList().also {
                                val current = it[point.x]
                                if (current == '#') {
                                    it[point.x] = '.'
                                } else {
                                    it[point.x] = '#'
                                }
                            }.joinToString("")
                        } else {
                            row
                        }
                    } else {
                        if (y == point.y) {
                            row.toMutableList().also {
                                val current = it[point.x]
                                if (current == '#') {
                                    it[point.x] = '.'
                                } else {
                                    it[point.x] = '#'
                                }
                            }.joinToString("")
                        } else {
                            row
                        }
                    }
                }

//                matrix.joinToString("\n").log("matrix\n")
//                newMatrix.joinToString("\n").log("new\n")

                val (ol, oa) = solve(matrix).log("solve")
                val (l, a) = solve(newMatrix, excludeCol = ol, excludeRow = oa).log("solve second")
                left += l.takeIf { it != ol } ?: 0
                above += a.takeIf { it != oa } ?: 0
                "left:$left above:$above".log()
            }

            left + above * 100
        }
        verify {
            expect result 30442
            run test 1 expect 400
        }
    }
}

private fun List<String>.foldAt(index: Int): Boolean {
    val (left, right) = withIndex().partition { (i, _) -> i < index }

    val count = left.reversed().zip(right)
        .takeWhile { (l, r) -> l.value == r.value }
        .count()

    return count == kotlin.math.min(left.size, right.size)
}

private fun List<String>.foldAt2(index: Int): Pair<Boolean, List<List<Int>>> {
    val (left, right) = withIndex().partition { (i, _) -> i < index }

    val diff = left.reversed().zip(right)
        .map { (l, r) ->
            l.value.zip(r.value).map { (a, b) ->
                if (a == b) {
                    0
                } else {
                    1
                }
            }

        }

    val count = left.reversed().zip(right)
        .takeWhile { (l, r) -> l.value == r.value }
        .count()

    return Pair(first = (count == kotlin.math.min(left.size, right.size)), second = diff)
}

private fun isDiffByOne(index: Int, isRow: Boolean, input: List<List<Int>>): Pair<Boolean, Point?> {
    val isOffByOne = (input.sumOf { it.sum() } == 1)
    if (!isOffByOne) {
        return false to null
    }

//    input.log("input: ")
    val indexOf = input.indexOfFirst { it.sum() == 1 }//.log("index: ")
    val indexOfDiff = input[indexOf].indexOfFirst { it == 1 }//.log("indexOfDiff: ")

    val point = if (isRow) {
        val x = indexOfDiff
        val y = index - indexOf - 1
        Point(x, y)
    } else {
        val x = index - indexOf - 1
        val y = indexOfDiff
        Point(x, y)
    }

    return true to point
}