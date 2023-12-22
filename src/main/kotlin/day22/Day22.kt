package day22

import common.Input
import common.day
import common.util.Point
import common.util.Point3D
import common.util.xy
import kotlin.math.abs
import kotlin.math.min

// answer #1: 448
// answer #2: 57770

fun main() {
    day(n = 22) {
        part1 { input ->
            val blocks = parseInput(input)
            val settledBlocks = letBlocksSettle(blocks)
            settledBlocks.size - findCriticalBlocks(settledBlocks).size
        }
        verify {
            expect result 448
            run test 1 expect 5
        }

        part2 { input ->
            val blocks = parseInput(input)
            val settledBlocks = letBlocksSettle(blocks)

            val blockFromPoint = settledBlocks.flatMap { block -> block.range.map { it to block } }.toMap()
            val blockStandingOn = mutableMapOf<Block, Set<Block>>().withDefault { emptySet() }
            val blockUpholding = mutableMapOf<Block, Set<Block>>().withDefault { emptySet() }
            settledBlocks.forEach { block ->
                val contactPoints = block.bottom
                    .map { it.copy(z = it.z - 1) }
                    .mapNotNull { point -> blockFromPoint[point] }
                    .toSet()

                blockStandingOn[block] = contactPoints
                contactPoints.forEach { bear ->
                    blockUpholding.merge(bear, setOf(block), Set<Block>::plus)
                }
            }

            findCriticalBlocks(settledBlocks).sumOf { b ->
                val queue = ArrayDeque<Block>()
                queue += blockUpholding.getValue(b)
                    .filter { blockStandingOn.getValue(it).size == 1 }
                val falling = queue.toMutableSet()

                while (queue.isNotEmpty()) {
                    val block = queue.removeFirst()

                    blockUpholding.getValue(block)
                        .filter { it !in falling }
                        .forEach { above ->
                            val contactPoints = blockStandingOn.getValue(above)
                            if (contactPoints.all { it in falling }) {
                                queue += above
                                falling += above
                            }
                        }
                }
                falling.size
            }
        }
        verify {
            expect result 57770
            run test 1 expect 7
        }
    }
}

private fun parseInput(input: Input) = input.lines.mapIndexed { index, line ->
    line.split("~").map { it.split(",") }
        .let { (a, b) ->
            Block(Point3D(a[0], a[1], a[2]), Point3D(b[0], b[1], b[2]), 'A' + index)
        }
}

private fun letBlocksSettle(blocks: List<Block>): List<Block> {
    val floorHeight = mutableMapOf<Point, Int>().withDefault { 0 }
    return blocks.sortedBy<Block, Int> { (start, end) -> min(start.z, end.z) }
        .map { block ->
            val (start, end) = block
            if (start.x == end.x && start.y == end.y) {
                // either vertical or single
                val height = abs(start.z - end.z)
                val floor = floorHeight.getValue(start.xy)
                val newStart = start.copy(z = floor + 1)
                val newEnd = end.copy(z = newStart.z + height)
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
}

private fun findCriticalBlocks(blocks: List<Block>): MutableSet<Block> {
    val blockToPointMap = blocks.flatMap { block -> block.range.map { it to block } }.toMap()
    val critical = mutableSetOf<Block>()
    blocks.forEach { block ->
        val contactPoints = block.bottom
            .map { point -> point.copy(z = point.z - 1) }
            .mapNotNull { point -> blockToPointMap[point] }
            .toSet()

        if (contactPoints.size == 1) {
            critical += contactPoints.single()
        }
    }
    return critical
}

private data class Block(val start: Point3D, val end: Point3D, val letter: Char) {
    val range = getRangeInternal()
    private val minZ = range.minOf { it.z }
    val bottom = range.filter { it.z == minZ }

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
