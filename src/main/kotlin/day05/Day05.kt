package day05

import common.Input
import common.day
import common.util.sliceByBlank

// answer #1: 340994526
// answer #2: 52210644

fun main() {
    day(n = 5) {
        part1 { input ->
            val (seeds, maps) = parseSeedsAndMaps(input)
            seeds.minOfOrNull { seed ->
                maps.fold(seed) { acc, map ->
                    map.firstNotNullOfOrNull { it.mapToDst(acc) } ?: acc
                }
            }
        }
        verify {
            expect result 340994526L
            run test 1 expect 35L
        }

        part2 { input ->
            val (seeds, mappingLevels) = parseSeedsAndMaps(input)

            val seedRanges = seeds
                .chunked(2)
                .map { (start, len) -> start..<start + len }
                .toMutableList()

            mappingLevels.forEach { level ->
                val queue = seedRanges.toMutableList()
                val movedSeedRanges = mutableListOf<LongRange>()

                while (queue.isNotEmpty()) {
                    val range = queue.removeFirst()

                    val mapper = level.firstOrNull { mapper -> mapper.intersect(range) }

                    when {
                        mapper == null -> movedSeedRanges.add(range)

                        range.first in mapper.srcRange && range.last in mapper.srcRange -> {
                            movedSeedRanges.add(mapper.mapToDst(range))
                        }

                        range.first in mapper.srcRange -> {
                            val spanToMove = range.first..mapper.srcRange.last
                            movedSeedRanges.add(mapper.mapToDst(spanToMove))

                            val remainder = spanToMove.last + 1..range.last
                            queue.add(remainder)
                        }

                        range.last in mapper.srcRange -> {
                            val spanToMove = mapper.srcRange.first..range.last
                            movedSeedRanges.add(mapper.mapToDst(spanToMove))

                            val remainder = range.first..<mapper.srcRange.first
                            queue.add(remainder)
                        }

                        else -> {
                            error("this should not happen")
                        }
                    }
                }

                seedRanges.clear()
                seedRanges.addAll(movedSeedRanges)
            }

            seedRanges.minOf(LongRange::first)
        }
        verify {
            expect result 52210644L
            run test 1 expect 46L
        }
    }
}

private data class Mapper(val dst: Long, val srcRange: LongRange) {
    private val n = dst - srcRange.first

    fun intersect(range: LongRange): Boolean =
        range.first in srcRange || range.last in srcRange

    fun mapToDst(range: LongRange): LongRange {
        return n + range.first..n + range.last
    }

    fun mapToDst(inputSrc: Long): Long? {
        return if (inputSrc in srcRange) {
            n + inputSrc
        } else {
            null
        }
    }
}

private fun parseSeedsAndMaps(input: Input): Pair<List<Long>, List<List<Mapper>>> {
    val inputs = input.lines.sliceByBlank()
    val seeds = inputs.first().first().drop(7).split(" ").map(String::toLong)
    val maps = inputs.drop(1)
        .map { it ->
            val mappers = it.drop(1)
                .map { it.split(" ").map(String::toLong) }
                .map { (dst, src, len) -> Mapper(dst, src ..< src + len) }
            mappers
        }

    return seeds to maps
}