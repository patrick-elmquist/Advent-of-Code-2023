package day22

import common.day
import common.util.Point
import common.util.Point3D
import common.util.xy
import kotlin.math.min

// answer #1:
// answer #2:

fun main() {
    day(n = 22) {
        part1 { input ->
            val blocks = input.lines.mapIndexed { index, line ->
                line.split("~").map { it.split(",") }
                    .let { (a, b) ->
                        Block(Point3D(a[0], a[1], a[2]), Point3D(b[0], b[1], b[2]), 'A' + index)
                    }
            }

            val floorHeight = mutableMapOf<Point, Int>().withDefault { 0 }
            val sorted = blocks.sortedBy { (start, end) -> min(start.z, end.z) }

            print(blocks, xAxis = Axis.X, yAxis = Axis.Z)
            println()
            print(blocks, xAxis = Axis.Y, yAxis = Axis.Z)

            val updated = sorted.map { block ->
                val (start, end) = block
                if (start.x == end.x && start.y == end.y) {
                    // either vertical or single
                    val order = listOf(start, end).sortedBy { it.z }
                    val floor = floorHeight.getValue(start.xy)
                    val newStart = start.copy(z = floor + 1)
                    val newEnd = end.copy(z = newStart.z + (order.last().z - order.first().z))
                    floorHeight[start.xy] = newEnd.z
                    block.copy(start = newStart, end = newEnd)
                } else if (start.x == end.x) {
                    // moving along y
                    val range = if (start.y < end.y) start.y..end.y else end.y..start.y
                    val floor = range.maxOf { y -> floorHeight.getValue(Point(start.x, y)) }
                    val newStart = start.copy(z = floor + 1)
                    val newEnd = end.copy(z = floor + 1)
                    range.forEach { y -> floorHeight[Point(start.x, y)] = floor + 1 }
                    block.copy(start = newStart, end = newEnd)
                } else {
                    // moving along x
                    val range = if (start.x < end.x) start.x..end.x else end.x..start.x
                    val floor = range.maxOf { x -> floorHeight.getValue(Point(x, start.y)) }
                    val newStart = start.copy(z = floor + 1)
                    val newEnd = end.copy(z = floor + 1)
                    range.forEach { x -> floorHeight[Point(x, start.y)] = floor + 1 }
                    block.copy(start = newStart, end = newEnd)
                }
            }

            println("Updated")
            print(updated, xAxis = Axis.X, yAxis = Axis.Z)
            println()
            print(updated, xAxis = Axis.Y, yAxis = Axis.Z)

            TODO()
        }
        verify {
            expect result null
            run test 1 expect Unit
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private data class Block(val start: Point3D, val end: Point3D, val letter: Char)

enum class Axis { X, Y, Z }

private fun print(b: List<Block>, xAxis: Axis, yAxis: Axis) {
    fun getField(block: Block, axis: Axis): Pair<Int, Int> {
        val (start, end) = block
        return when (axis) {
            Axis.X -> start.x to end.x
            Axis.Y -> start.y to end.y
            Axis.Z -> start.z to end.z
        }
    }

    val blocks = b.sortedWith { b1, b2 ->
        val (b1StartY, b1EndY) = getField(b1, axis = Axis.Y)
        val (b2StartY, b2EndY) = getField(b2, axis = Axis.Y)
        min(b1StartY, b1EndY).compareTo(min(b2StartY, b2EndY))
    }

    val minMaxX = blocks.minOf { block -> getField(block, xAxis).let { minOf(it.first, it.second) } }..
            blocks.maxOf { block -> getField(block, xAxis).let { maxOf(it.first, it.second) } }

    val minMaxY = blocks.maxOf { block -> getField(block, yAxis).let { maxOf(it.first, it.second) } } downTo
            blocks.minOf { block -> getField(block, yAxis).let { minOf(it.first, it.second) } }

    val map = blocks.flatMap { block ->
        val (startX, endX) = getField(block, xAxis)
        val xRange = if (startX < endX) startX..endX else endX..startX

        val (startY, endY) = getField(block, yAxis)
        val yRange = if (startY < endY) startY..endY else endY..startY

        if (startX == endX && startY == endY) {
            // either single block or perpendicular
            listOf(Point(startX, startY) to block.letter)
        } else if (startX == endX) {
            yRange.map { y -> Point(startX, y) to block.letter }
        } else {
            xRange.map { x -> Point(x, startY) to block.letter }
        }
    }.toMap()

    println("$xAxis x $yAxis")
    println("---------------")
    for (y in minMaxY) {
        for (x in minMaxX) {
            val l = map[Point(x, y)]
            if (l != null) {
                print(l)
            } else {
                print('.')
            }
        }
        print(" $y")
        println()
    }
    println()
}
