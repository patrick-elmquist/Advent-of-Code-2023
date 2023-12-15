package day15

import common.day
import java.util.*

// answer #1: 502139
// answer #2: 284132

fun main() {
    day(n = 15) {
        part1 { input ->
            input.lines.first().split(",").sumOf(::hash)
        }
        verify {
            expect result 502139
            run test 1 expect 1320
        }

        part2 { input ->
            val boxes = List(256) { LinkedList<Pair<String, String>>() }
            input.lines.first().split(",").forEach { step ->
                when {
                    '-' in step -> {
                        val label = step.dropLast(1)
                        val content = boxes[hash(label)]
                        val index = content.indexOfFirst { it.first == label }
                        if (index >= 0) content.removeAt(index)
                    }

                    '=' in step -> {
                        val (label, focalLength) = step.split("=")
                        val content = boxes[hash(label)]
                        val index = content.indexOfFirst { it.first == label }
                        if (index >= 0) {
                            content[index] = label to focalLength
                        } else {
                            content.add(label to focalLength)
                        }
                    }
                }
            }

            boxes.withIndex().sumOf { (boxIndex, lenses) ->
                val sum = lenses.withIndex().sumOf { (indexInBox, entry) ->
                    (indexInBox + 1) * entry.second.toInt()
                }
                (boxIndex + 1) * sum
            }
        }
        verify {
            expect result 284132
            run test 1 expect 145
        }
    }
}

private fun hash(it: String) = it.fold(0) { current, c -> (current + c.code) * 17 % 256 }