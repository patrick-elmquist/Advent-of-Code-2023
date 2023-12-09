package day08

import common.Input
import common.day
import common.util.leastCommonMultiple

// answer #1: 20777
// answer #2: 13289612809129

fun main() {
    day(n = 8) {
        part1 { input ->
            val (instructions, mappers) = parseInstructionsAndMappers(input)
            findLoopDistance(start = "AAA", instructions = instructions, mappers = mappers)
        }
        verify {
            expect result 20777L
            run test 1 expect 2L
            run test 2 expect 6L
        }

        part2 { input ->
            val (instructions, mappers) = parseInstructionsAndMappers(input)
            mappers.keys
                .filter { it.endsWith('A') }
                .map { start -> findLoopDistance(start, instructions, mappers) }
                .reduce { acc, distance -> leastCommonMultiple(acc, distance) }
        }
        verify {
            expect result 13289612809129L
            run test 3 expect 6L
        }
    }
}

private fun findLoopDistance(
    start: String,
    instructions: String,
    mappers: Map<String, Pair<String, String>>
): Long {
    var current = start
    var counter = 0L
    while (!current.endsWith('Z')) {
        val (left, right) = mappers.getValue(current)
        val index = counter % instructions.length.toLong()
        val instruction = instructions[index.toInt()]
        current = when (instruction) {
            'L' -> left
            'R' -> right
            else -> error("")
        }
        counter++
    }
    return counter
}

val regex = """(\w{3}) = \((\w{3}), (\w{3})\)""".toRegex()
private fun parseInstructionsAndMappers(input: Input): Pair<String, Map<String, Pair<String, String>>> =
    input.lines.first() to input.lines.drop(2)
        .mapNotNull { line -> regex.matchEntire(line)?.destructured }
        .associate { (key, left, right) -> key to Pair(left, right) }
