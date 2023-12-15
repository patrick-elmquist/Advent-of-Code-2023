package day15

import common.day

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
            val boxes = List(256) { mutableMapOf<String, String>() }
            input.lines.first().split(",").forEach { step ->
                val (label, focalLength) = step.split("=", "-")
                val box = boxes[hash(label)]
                when {
                    '-' in step -> box.remove(label)
                    '=' in step -> box.merge(label, focalLength) { _, b -> b }
                }
            }

            boxes
                .map { lenses ->
                    lenses.values
                        .withIndex()
                        .sumOf { (indexInBox, focalLength) ->
                            (indexInBox + 1) * focalLength.toInt()
                        }
                }
                .withIndex()
                .sumOf { (boxIndex, focalLengthSum) -> (boxIndex + 1) * focalLengthSum }
        }
        verify {
            expect result 284132
            run test 1 expect 145
        }
    }
}

private fun hash(it: String) = it.fold(0) { current, c -> (current + c.code) * 17 % 256 }