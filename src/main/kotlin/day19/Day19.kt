package day19

import common.day
import common.util.log
import common.util.sliceByBlank

// answer #1: 391132
// answer #2:

fun main() {
    day(n = 19) {
        part1 { input ->
            val (steps, parts) = input.lines.sliceByBlank().let { (a, b) ->
                parseSteps(a) to parseParts(b)
            }

            steps.log("steps:")
            println()
            parts.log("parts:")

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

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
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
    private val checks = split.dropLast(1)
    fun run(part: Part): Result {
        val next = checks.firstNotNullOfOrNull {
            val (condition, ifTrue) = it.split(":")
            condition.log("condition:")
            val eval = when {
                '<' in condition -> {
                    condition.split("<").let { (a, b) ->
                        part.getValue(a.first()) < b.toInt()
                    }
                }

                '>' in condition -> {
                    condition.split(">").let { (a, b) ->
                        part.getValue(a.first()) > b.toInt()
                    }
                }

                else -> error("")
            }
            ifTrue.takeIf { eval }
        } ?: default
        return Result.from(next)
    }
}

