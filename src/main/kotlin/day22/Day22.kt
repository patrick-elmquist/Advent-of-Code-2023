package day22

import common.day
import common.util.Point
import common.util.Point3D
import common.util.xy
import kotlin.math.min

// answer #1: 448
// answer #2: 57770

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
            val bearing = findCriticalBlocks(updated)
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

            val pointToBlockMap = updated.flatMap { block -> block.range.map { it to block } }.toMap()
            val blockUpheldBy = mutableMapOf<Block, Set<Block>>()
            val blockUpholding = mutableMapOf<Block, Set<Block>>()
            updated.forEach { block ->
                val restingOn = block.bottom.map { it.copy(z = it.z - 1) }
                val restingPoints = restingOn
                    .mapNotNull { pointToBlockMap[it] }
                    .distinct()

                blockUpheldBy[block] = restingPoints.toSet()
                restingPoints.forEach { bear ->
                    blockUpholding.merge(bear, setOf(block)) { a, b -> a + b }
                }
            }
//            blockUpholding.map { it.key.letter to it.value.map { it.letter } } log "upholding "
//            blockUpheldBy.map { it.key.letter to it.value.map { it.letter } } log "upheld by "

            var total = 0
            updated.forEach { b ->
                val q = ArrayDeque<Block>()
                q += (blockUpholding[b] ?: emptySet()).filter {
                    (blockUpheldBy[it] ?: emptySet()).size == 1
                }
//                println("START ${b.letter} adding ${q.map { it.letter }} to queue")
                val falling = q.toMutableSet()

                while (q.isNotEmpty()) {
                    val j = q.removeFirst()
//                    println("${j.letter} checking")

                    val blocks = blockUpholding[j] ?: emptySet()
                    blocks
                        .also {
                            val upholding = it.map { it.letter }
//                            println("${j.letter} holding up $upholding")
                        }
                        .filter { it !in falling }
                        .forEach {
                            val filter = blockUpheldBy.getValue(it).filter { it !in falling }
                            if (filter.isEmpty()) {
//                                println("${it.letter} now falling adding to queue")
                                q += it
                                falling += it
                            } else {
//                                println("${it.letter} not falling ${filter.size}")
                            }
                        }
                }
                total += falling.size
//                println("new total $total new:${falling.size - 1}")
            }
            total
        }
        verify {
            expect result 57770
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

private fun findCriticalBlocks(updated: List<Block>): MutableSet<Block> {
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
    val lowest = range.minOf { it.z }
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
