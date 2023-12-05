package day05

import common.day
import common.util.log
import common.util.sliceByBlank
import kotlinx.coroutines.*

// answer #1: 340994526
// answer #2: 52210644

data class Converter(val dst: Long, val src: Long, val len: Long) {
    private val srcRange = src..src+len
    fun convert(inputSrc: Long): Long {
        return if (inputSrc in srcRange) {
            dst + (inputSrc - src)
        } else {
            return -1
        }
    }
}

fun main() {
    day(n = 5) {
        part1 { input ->
            val inputs = input.lines.sliceByBlank()
            val seeds = inputs.first().first().drop(7).split(" ").map(String::toLong)
            val maps = inputs.drop(1)
                .mapIndexed { index, it ->
                    val converters = it.drop(1)
                        .map { it.split(" ").map { it.toLong() } }
                        .map { (dst, src, len) -> Converter(dst, src, len) }
                    index to converters
                }

            seeds.minOfOrNull { seed ->
                maps.foldIndexed(seed) { _, acc, (_, converters) ->
                    val result = converters.firstNotNullOfOrNull { it.convert(acc).takeIf { it >= 0 } }
                    result ?: acc
                }
            }
        }
        verify {
            expect result 340994526L
            run test 1 expect 35L
        }

        part2 { input ->
            val inputs = input.lines.sliceByBlank()
            val seeds = inputs.first().first().drop(7).split(" ").map(String::toLong)
                .chunked(2)
                .map { (start, len) ->
                    start..start+len
                }

            val
            seeds

            val maps = inputs.drop(1)
                .map { it ->
                    val converters = it.drop(1)
                        .map { it.split(" ").map { it.toLong() } }
                        .map { (dst, src, len) -> Converter(dst, src, len) }
                    converters
                }

            runBlocking {
                withContext(Dispatchers.Default) {
                    seeds.map { range ->
                        async {
                            range.minOf { seed ->
                                maps.fold(seed) { acc, converters ->
                                    val result = converters.firstNotNullOfOrNull { it.convert(acc).takeIf { it >= 0 } }
                                    result ?: acc
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