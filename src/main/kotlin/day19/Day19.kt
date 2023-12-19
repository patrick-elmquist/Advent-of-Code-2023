package day19

import common.day
import common.util.sliceByBlank

// answer #1: 391132
// answer #2: 128163929109524

fun main() {
    day(n = 19) {
        part1 { input ->
            val (steps, parts) = input.lines.sliceByBlank().let { (a, b) ->
                parseSteps(a) to parseParts(b)
            }
            val startStep = steps.getValue("in")
            parts
                .filter { part ->
                    runSteps(startStep, part, steps) is Result.Accepted
                }
                .sumOf { it.x + it.m + it.a + it.s }
        }
        verify {
            expect result 391132
            run test 1 expect 19114
        }

        part2 { input ->
            val steps = input.lines.sliceByBlank().first().let {steps ->
                steps.associate { line ->
                    val name = line.takeWhile { it != '{' }
                    val checks = line.removePrefix(name).drop(1).dropLast(1).split(",")
                    name to checks
                }
            }

            val acceptedRanges = mutableListOf<PartRange>()
            val queue = ArrayDeque<Pair<String, PartRange>>()
            queue += "in" to PartRange()

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
    startStep: Step,
    part: Part,
    steps: Map<String, Step>
): Result.Terminal {
    var nextStep = startStep
    while (true) {
        when (val result = nextStep.run(part)) {
            is Result.Terminal -> return result
            is Result.Next -> nextStep = steps.getValue(result.name)
        }
    }
}

private fun parseParts(parts: List<String>): List<Part> =
    parts.map { line ->
        line.removeSurrounding(prefix = "{", suffix = "}")
            .let { it.split(",").map { it.split("=").last().toInt() } }
            .let { (x, m, a, s) -> Part(x, m, a, s) }
    }

private fun parseSteps(rules: List<String>): Map<String, Step> =
    rules.associate {
        val name = it.takeWhile { it != '{' }
        val step = Step(it.removePrefix(name))
        name to step
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

private sealed interface Result {
    sealed interface Terminal : Result
    data object Accepted : Terminal
    data object Rejected : Terminal
    data class Next(val name: String) : Result
    companion object {
        fun from(string: String): Result {
            return when (string) {
                "A" -> Accepted
                "R" -> Rejected
                else -> Next(string)
            }
        }
    }
}

private data class Step(val input: String) {
    private val split = input.removeSurrounding(prefix = "{", suffix = "}").split(",")
    private val default = split.last()
    val checks = split.dropLast(1).map { Check(it) } + Default(default)

    sealed interface ICheck {
        val outcome: String
        fun execute(part: Part): Boolean
    }

    data class Default(override val outcome: String) : ICheck {
        override fun execute(part: Part): Boolean = true
    }

    data class Check(val string: String) : ICheck {
        val symbol = if ('<' in string) '<' else '>'
        private val split = string.split(':')
        override val outcome = split.last()

        private val condition = split.first().split(symbol)
        val field = condition.first().first()
        val n = condition.last().toInt()

        override fun execute(part: Part): Boolean =
            when (symbol) {
                '<' -> part.getValue(field) < n
                '>' -> part.getValue(field) > n
                else -> error("")
            }
    }

    fun run(part: Part): Result {
        val next = checks.firstNotNullOfOrNull { check ->
            check.outcome.takeIf {
                check.execute(part)
            }
        } ?: default
        return Result.from(next)
    }
}
