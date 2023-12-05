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
                    map.firstNotNullOfOrNull { it.convert(acc).takeIf { it >= 0 } }
                        ?: acc
                }
            }
        }
        verify {
            expect result 340994526L
            run test 1 expect 35L
        }

        part2 { input ->
            val (seeds, maps) = parseSeedsAndMaps(input)
            val seedRanges = seeds
                .chunked(2)
                .map { (start, len) -> start..<start + len }

            val ranges = seedRanges.toMutableList()

            maps.forEach { level ->
                val queue = ranges.toMutableList()
                val newRanges = mutableListOf<LongRange>()

                while (queue.isNotEmpty()) {
                    val range = queue.removeFirst()

                    val mapper = level.firstOrNull { mapper ->
                        if (range.first in mapper.srcRange) {
                            true
                        } else if (range.last in mapper.srcRange) {
                            true
                        } else {
                            false
                        }
                    }

                    if (mapper == null) {
                        newRanges.add(range)
                        continue
                    }

                    if (range.first in mapper.srcRange && range.last in mapper.srcRange) {
                        newRanges.add(mapper.convert(range.first)..mapper.convert(range.last))
                    } else if (range.first in mapper.srcRange) {
                        val newSpan = range.first..mapper.srcRange.last
                        newRanges.add(mapper.convert(newSpan.first)..mapper.convert(newSpan.last))

                        val toBeProcessed = newSpan.last + 1..range.last
                        queue.add(toBeProcessed)
                    } else if (range.last in mapper.srcRange) {
                        val newSpan = mapper.srcRange.first..range.last
                        newRanges.add(mapper.convert(newSpan.first)..mapper.convert(newSpan.last))

                        val toBeProcessed = range.first..<mapper.srcRange.first
                        queue.add(toBeProcessed)
                    } else {
                        error("this should not happen")
                    }
                }
                ranges.clear()
                ranges.addAll(newRanges)
            }
            ranges.minOf { it.first }
        }
        verify {
            expect result 52210644L
            run test 1 expect 46L
        }
    }
}

private data class Mapper(val dst: Long, val src: Long, val len: Long) {
    val srcRange = src..<src + len
    private val n = dst - src
    fun convert(inputSrc: Long): Long {
        return if (inputSrc in srcRange) {
            n + inputSrc
        } else {
            return -1
        }
    }
}

private fun parseSeedsAndMaps(input: Input): Pair<List<Long>, List<List<Mapper>>> {
    val inputs = input.lines.sliceByBlank()
    val seeds = inputs.first().first().drop(7).split(" ").map(String::toLong)
    val maps = inputs.drop(1)
        .map { it ->
            val mappers = it.drop(1)
                .map { it.split(" ").map { it.toLong() } }
                .map { (dst, src, len) -> Mapper(dst, src, len) }
            mappers
        }

    return seeds to maps
}