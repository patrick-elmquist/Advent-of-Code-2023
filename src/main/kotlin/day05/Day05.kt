package day05

import common.Input
import common.day
import common.util.sliceByBlank
import kotlinx.coroutines.*

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
                .map { (start, len) -> start..start + len }

            runBlocking {
                withContext(Dispatchers.Default) {
                    seedRanges.map { range ->
                        async {
                            range.minOf { seed ->
                                maps.fold(seed) { acc, map ->
                                    map.firstNotNullOfOrNull { it.convert(acc).takeIf { it >= 0 } }
                                        ?: acc
                                }
                            }
                        }
                    }.awaitAll().min()
                }
            }
        }
        verify {
            expect result 52210644L
            run test 1 expect 46L
        }
    }
}

private data class Converter(val dst: Long, val src: Long, val len: Long) {
    private val srcRange = src..src + len
    fun convert(inputSrc: Long): Long {
        return if (inputSrc in srcRange) {
            dst + (inputSrc - src)
        } else {
            return -1
        }
    }
}

private fun parseSeedsAndMaps(input: Input): Pair<List<Long>, List<List<Converter>>> {
    val inputs = input.lines.sliceByBlank()
    val seeds = inputs.first().first().drop(7).split(" ").map(String::toLong)
    val maps = inputs.drop(1)
        .map { it ->
            val converters = it.drop(1)
                .map { it.split(" ").map { it.toLong() } }
                .map { (dst, src, len) -> Converter(dst, src, len) }
            converters
        }

    return seeds to maps
}