package day19

import common.day
import common.util.arrayDequeOf
import common.util.sliceByBlank

// answer #1: 391132
// answer #2: 128163929109524

fun main() {
    day(n = 19) {
        part1 { input ->
            val sections = input.lines.sliceByBlank()
            val steps = sections.first().associate { line ->
                val name = line.takeWhile { it != '{' }
                val checks = line.removePrefix(name).drop(1).dropLast(1).split(",")
                name to checks
            }
            parseParts(sections.last())
                .filter { part -> runSteps(part, steps) }
                .sumOf { it.x + it.m + it.a + it.s }
        }
        verify {
            expect result 391132
            run test 1 expect 19114
        }

        part2 { input ->
            val steps = input.lines.sliceByBlank().first().let { steps ->
                steps.associate { line ->
                    val name = line.takeWhile { it != '{' }
                    val checks = line.removePrefix(name).drop(1).dropLast(1).split(",")
                    name to checks
                }
            }

            val acceptedRanges = mutableListOf<PartRange>()
            val queue = arrayDequeOf("in" to PartRange())

            while (queue.isNotEmpty()) {
                val (name, range) = queue.removeFirst()

                if (name == "R") continue
                if (name == "A") {
                    acceptedRanges += range
                    continue
                }

                val step = steps.getValue(name)
                var remaining = range
                step.forEach { check ->
                    val simpleCheck = parseCheck(check)
                    if (simpleCheck == null) {
                        queue += check to remaining
                    } else {
                        val (field, symbol, n, outcome) = simpleCheck
                        val category = remaining.getValue(field)
                        if (symbol == '<') {
                            val mutate = remaining.copyWithField(field, category.first..<n)
                            remaining = remaining.copyWithField(field, n..category.last)
                            queue += outcome to mutate
                        } else {
                            val mutate = remaining.copyWithField(field, n + 1..category.last)
                            remaining = remaining.copyWithField(field, category.first..n)
                            queue += outcome to mutate
                        }
                    }
                }
            }

            acceptedRanges.sumOf { it.x.len() * it.m.len() * it.a.len() * it.s.len() }
        }
        verify {
            expect result 128163929109524L
            run test 1 expect 167409079868000L
        }
    }
}

private data class SimpleCheck(val field: Char, val symbol: Char, val n: Int, val outcome: String)

private val regex = """(\w)([<>])(\d+):(\w+)""".toRegex()
private fun parseCheck(check: String): SimpleCheck? =
    regex.matchEntire(check)
        ?.destructured
        ?.let { (a, b, c, d) -> SimpleCheck(a.first(), b.first(), c.toInt(), d) }

private fun IntRange.len(): Long = (last - first + 1).toLong()

private data class PartRange(
    val x: IntRange = 1..4000,
    val m: IntRange = 1..4000,
    val a: IntRange = 1..4000,
    val s: IntRange = 1..4000,
) {
    fun getValue(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("")
    }

    fun copyWithField(c: Char, range: IntRange) = when (c) {
        'x' -> copy(x = range)
        'm' -> copy(m = range)
        'a' -> copy(a = range)
        's' -> copy(s = range)
        else -> error("")
    }
}

private fun runSteps(
    part: Part,
    steps: Map<String, List<String>>
): Boolean {
    var nextStep = steps.getValue("in")
    while (true) {
        val next = nextStep.firstNotNullOf { check ->
            val simpleCheck = parseCheck(check)
            if (simpleCheck == null) {
                check
            } else {
                val (field, symbol, n, outcome) = simpleCheck
                if (symbol == '<') {
                    outcome.takeIf { part.getValue(field) < n }
                } else {
                    outcome.takeIf { part.getValue(field) > n }
                }
            }

        }
        if (next in setOf("A", "R")) {
            return next == "A"
        }
        nextStep = steps.getValue(next)
    }
}

private fun parseParts(parts: List<String>): List<Part> =
    parts.map { line ->
        line.removeSurrounding(prefix = "{", suffix = "}")
            .let { it.split(",").map { it.split("=").last().toInt() } }
            .let { (x, m, a, s) -> Part(x, m, a, s) }
    }

private data class Part(val x: Int, val m: Int, val a: Int, val s: Int) {
    fun getValue(c: Char) = when (c) {
        'x' -> x
        'm' -> m
        'a' -> a
        's' -> s
        else -> error("")
    }
}
