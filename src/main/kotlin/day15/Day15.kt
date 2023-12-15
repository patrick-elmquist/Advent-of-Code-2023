package day15

import common.day
import common.util.log
import java.util.*

// answer #1: 502139
// answer #2:

fun main() {
    day(n = 15) {
        part1 { input ->
            input.lines.first()
                .split(",")
                .sumOf { hash(it) }
        }
        verify {
            expect result 502139
            run test 1 expect 1320
        }

        part2 { input ->
            val boxes = List(256) { LinkedList<Pair<String, Int>>() }
            input.lines.first().split(",").forEach { input ->
                when {
                    '-' in input -> {
                        val label = input.dropLast(1)
                        val box = hash(label)
                        val list = boxes[box]
                        val index = list.indexOfFirst { it.first == label }
                        if (index >= 0) {
                            list.removeAt(index)
                        }
                    }

                    '=' in input -> {
                        val (label, focal) = input.split("=").let { (label, fl) -> label to fl.toInt() }
                        val box = hash(label)
                        val list = boxes[box]
                        val index = list.indexOfFirst { it.first == label }
                        if (index >= 0) {
                            list[index] = label to focal
                        } else {
                            list.add(label to focal)
                        }
                    }
                }
                "After \"$input\":".log()
                boxes.withIndex()
                    .filter { it.value.isNotEmpty() }
                    .forEach { (i, box) ->
                        val content = box.joinToString { "[${it.first} ${it.second}]" }
                        "Box $i $content".log()
                    }

                println()
            }

            boxes.withIndex().sumOf { (boxIndex, lenses) ->
                val lenseSum = lenses.withIndex().sumOf { (indexInBox, entry) ->
                    (indexInBox + 1) * entry.second
                }
                (boxIndex + 1) * lenseSum
            }
        }
        verify {
            expect result null
            run test 1 expect 145
        }
    }
}

private fun hash(it: String) =
    it.fold(0) { current, c ->
        (current + c.code) * 17 % 256
    }