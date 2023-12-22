package day22

import common.day
import common.util.Point
import common.util.Point3D
import common.util.log
import common.util.xy
import kotlin.math.min

// answer #1: 448
// answer #2: not 102227, too high
//            not 102215, too high

fun main() {
    day(n = 22) {
        part1 { input ->
            val blocks = input.lines.mapIndexed { index, line ->
                line.split("~").map { it.split(",") }
                    .let { (a, b) ->
                        Block(Point3D(a[0], a[1], a[2]), Point3D(b[0], b[1], b[2]), 'A' + index)
                    }
            }
            val sorted = blocks.sortedBy { (start, end) -> min(start.z, end.z) }
            val updated = compress(sorted)
            val bearing = findBearingBlocks(updated)
            updated.size - bearing.size
        }
        verify {
            expect result 448
            run test 1 expect 5
        }

        part2 { input ->
            val init = input.lines.mapIndexed { index, line ->
                line.split("~").map { it.split(",") }
                    .let { (a, b) ->
                        Block(Point3D(a[0], a[1], a[2]), Point3D(b[0], b[1], b[2]), 'A' + index)
                    }
            }
            val updated = compress(init.sortedBy<Block, Int> { (start, end) -> min(start.z, end.z) })
            val bearing = findBearingBlocks(updated)

            val pointToBlockMap = updated.flatMap { block -> block.range.map { it to block } }.toMap()
            // calculate a map of a block and all who directly depend on it
            val blockIsBearing = mutableMapOf<Block, MutableList<Block>>()
            val blockRestingOn = mutableMapOf<Block, List<Block>>()
            updated.forEach { block ->
                val restingOn = block.bottom.map { it.copy(z = it.z - 1) }
                val restingPoints = restingOn
                    .mapNotNull { pointToBlockMap[it] }
                    .distinct()

                blockRestingOn[block] = restingPoints

                restingPoints.forEach { bear ->
                    val list = blockIsBearing.getOrPut(bear) { mutableListOf() }
                    list.add(block)
                }
            }

            fun rec(
                block: Block,
                blockIsLifting: Map<Block, List<Block>>,
                blockIsRestingOn: Map<Block, List<Block>>,
                memo: MutableMap<Block, Set<Block>>,
                visited: MutableSet<Block>,
            ): Set<Block> {
                if (block in visited) {
                    println("${block.letter} ignoring")
                    return emptySet()
                }
                visited += block

                println("${block.letter} visiting")
                return memo.getOrPut(block) {
                    block.above.mapNotNull { pointToBlockMap }.forEach {
                        blockRestingOn

                    }

                    TODO()
                }.also { println("${block.letter} returning ${it.map { it.letter }}") }
            }

            val memo = mutableMapOf<Block, Set<Block>>()
            val sum = bearing.sortedByDescending { it.lowest }
                .sumOf {
                    println(it.letter)
                    rec(
                        block = it,
                        blockIsLifting = blockIsBearing,
                        blockIsRestingOn = blockRestingOn,
                        memo = memo,
                        visited = mutableSetOf()
                    ).size
                }

            memo.map { it.key.letter to it.value.map { it.letter } } log "memo"

            sum log "sum"
        }
        verify {
            expect result null
            run test 1 expect 7
        }
    }
}

private fun compress(sorted: List<Block>): List<Block> {
    val floorHeight = mutableMapOf<Point, Int>().withDefault { 0 }
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
    return updated
}

private fun findBearingBlocks(updated: List<Block>): MutableSet<Block> {
    val pointToBlockMap = updated.flatMap { block -> block.range.map { it to block } }.toMap()
    val vital = mutableSetOf<Block>()
    updated.forEach { block ->
        val restingOn = block.bottom.map { it.copy(z = it.z - 1) }
        val restingPoints = restingOn
            .mapNotNull { pointToBlockMap[it] }
            .distinct()

        if (restingPoints.size == 1) {
            vital += restingPoints.single()
        }
    }
    return vital
}

private data class Block(val start: Point3D, val end: Point3D, val letter: Char) {
    val range = getRangeInternal()
    val highest = range.maxOf { it.z }
    val lowest = range.minOf { it.z }
    val above = range.filter { it.z == highest }.map { it.copy(z = highest + 1) }
    val bottom = range.filter { it.z == lowest }

    private fun getRangeInternal(): List<Point3D> {
        val rangeX = if (start.x < end.x) start.x..end.x else end.x..start.x
        val rangeY = if (start.y < end.y) start.y..end.y else end.y..start.y
        val rangeZ = if (start.z < end.z) start.z..end.z else end.z..start.z
        val list = mutableListOf<Point3D>()
        for (x in rangeX) {
            for (y in rangeY) {
                for (z in rangeZ) {
                    list += Point3D(x, y, z)
                }
            }
        }
        return list
    }
}

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
