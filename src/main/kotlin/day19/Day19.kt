package day19

import common.day
import common.util.log
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
            parts.filter { part ->
                runSteps(startStep, part, steps) is Result.Accepted
            }.sumOf { it.x + it.m + it.a + it.s }
        }
        verify {
            expect result 391132
            run test 1 expect 19114
        }

        part2 { input ->
            val steps = input.lines.sliceByBlank().let { parseSteps(it.first()) }

            val acceptedRanges = mutableListOf<PartRange>()
            val rangesToInvestigate = ArrayDeque<Pair<String, PartRange>>()
            rangesToInvestigate += "in" to PartRange()

            while (rangesToInvestigate.isNotEmpty()) {
                val (name, range) = rangesToInvestigate.removeFirst()

                if (name == "R") continue
                if (name == "A") {
                    acceptedRanges += range
                    continue
                }

                val step = steps.getValue(name)
                var remaining = range
                val checks = step.checks
                checks.forEach { check ->
                    when (check) {
                        is Step.Check -> {
                            val field = check.field
                            val category = remaining.getValue(field)
                            val n = check.n

                            if (n in category) {
                                if (check.symbol == '<') {
                                    rangesToInvestigate += check.outcome to remaining.setValue(
                                        field,
                                        category.first..<n
                                    )
                                    remaining = remaining.setValue(field, n..category.last)
                                } else {
                                    rangesToInvestigate += check.outcome to remaining.setValue(
                                        field,
                                        n + 1..category.last
                                    )
                                    remaining = remaining.setValue(field, category.first..n)
                                }
                            }
                        }

                        is Step.Default -> {
                            rangesToInvestigate += check.outcome to remaining
                        }
                    }
                }
            }

            fun IntRange.len(): Long = (last - first + 1).toLong()

            acceptedRanges.sumOf {
                it.x.len() * it.m.len() * it.a.len() * it.s.len()
            }
        }
        verify {
            expect result 128163929109524L
            run test 1 expect 167409079868000L
        }
    }
}

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

    fun setValue(c: Char, range: IntRange) = when (c) {
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
    object Accepted : Terminal
    object Rejected : Terminal
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
            check.outcome.takeIf { check.execute(part) }
        } ?: default
        return Result.from(next)
    }
}
