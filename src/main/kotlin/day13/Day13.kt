package day13

import common.day
import common.util.log
import common.util.sliceByBlank
import kotlin.math.abs
import kotlin.math.max

// answer #1: 35538
// answer #2:

fun main() {
    day(n = 13) {
        fun parseColsAndRows(matrix: List<String>): Pair<List<String>, List<String>> {
            val cols = (0..<matrix.first().length).map { c ->
                matrix.map { it[c] }.joinToString("")
            }
            return cols to matrix
        }

        part1 { input ->
            var left = 0
            var above = 0
            input.lines.sliceByBlank().forEach { matrix ->
                val (cols, rows) = parseColsAndRows(matrix)

                for (i in 1..cols.lastIndex) {
                    val diff = cols.foldAt(i)
                    if (diff) {
                        left += i
                        break
                    }
                }

                for (i in 1..rows.lastIndex) {
                    val diff = rows.foldAt(i)
                    if (diff) {
                        above += i
                        break
                    }
                }
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
            input.lines.sliceByBlank().forEach { matrix ->
                val (cols, rows) = parseColsAndRows(matrix)

                var maxCol = 0
                var maxColIndex = 0
                for (i in 1..cols.lastIndex) {
                    val (count, diff) = cols.foldAt2(i)
                    if (diff) {
                        maxCol = count
                        maxColIndex = i
                        break
                    }
                    "col i:$i count: $count perfect:$diff".log()
                }

                var maxRow = 0
                var maxRowIndex = 0
                for (i in 1..rows.lastIndex) {
                    val (count, diff) = rows.foldAt2(i)
                    if (diff) {
                        maxRow = count
                        maxRowIndex = i
                        break
                    }
                    "row i:$i count: $count perfect:$diff".log()
                }

                println()

                if (maxCol > maxRow) {
                    left += maxColIndex
                    maxRowIndex.log("maxRowIndex")
                    maxColIndex.log("maxColIndex winner")
                } else {
                    above += maxRowIndex
                    maxRowIndex.log("maxRowIndex winner")
                    maxColIndex.log("maxColIndex")
                }
            }
            left + above * 100
        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun List<String>.foldAt(index: Int): Boolean {
    var (left, right) = withIndex().partition { (i, _) -> i < index }

    left = left.sortedBy { it.index }
    right = right.sortedBy { it.index }

    val count = left.reversed().zip(right)
        .takeWhile { (l, r) -> l.value == r.value }
        .count()

    return count == kotlin.math.min(left.size, right.size)
}

private fun List<String>.foldAt2(index: Int): Pair<Int, Boolean> {
    var (left, right) = withIndex().partition { (i, string) -> i < index }

    left = left.sortedBy { it.index }
    right = right.sortedBy { it.index }

    val count = left.reversed().zip(right).takeWhile { (l, r) ->
        l.value == r.value
    }.count()

    return count to (count == kotlin.math.min(left.size, right.size))
}
