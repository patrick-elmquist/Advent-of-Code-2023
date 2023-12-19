package day19

import common.day
import common.util.log
import common.util.sliceByBlank
import kotlin.math.max
import kotlin.math.min

// answer #1: 391132
// answer #2:

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

            val (steps, parts) = input.lines.sliceByBlank().let { (a, b) ->
                parseSteps(a) to parseParts(b)
            }

            // PartRange(
            //   x = 1..4000,
            //   m = 1..4000,
            //   a = 1..4000,
            //   s = 1..4000,
            // )
            // in{s<1351:px,qqz}
            // true
            // px = PartRange(
            //   x = 1..4000,
            //   m = 1..4000,
            //   a = 1..4000,
            //   s = 1..1350,
            // )
            // false
            // qqz = PartRange(
            //   x = 1..4000,
            //   m = 1..4000,
            //   a = 1..4000,
            //   s = 1351..4000,
            // )

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
                                        category.first ..< n
                                    )
                                    remaining = remaining.setValue(field, n .. category.last)
                                } else {
                                    rangesToInvestigate += check.outcome to remaining.setValue(
                                        field,
                                        n + 1 .. category.last
                                    )
                                    remaining = remaining.setValue(field, category.first .. n)
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
            acceptedRanges.size.log("accepted: ")
            acceptedRanges.sumOf {
                it.x.len() * it.m.len() * it.a.len() * it.s.len()
            }
        }
        verify {
            expect result null
            run test 1 expect 167409079868000L
        }
    }
}

private fun oldTry() {
    /*    val endStates = steps.filter { (_, step) -> 'A' in step.input }
        endStates.log()
        fun IntRange.len() = (last - first + 1).toLong()
        val ranges = endStates.flatMap { endStep ->
            endStep.log()
            val range = findRange(
                current = endStep,
                trace = "A",
                steps = steps,
                range = PartRange(),
            )
            range.log().also { println() }
        }
            .sumOf {
                it.x.len() * it.m.len() * it.a.len() * it.s.len()
            }
        ranges.log()*/

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

private fun findRange(
    current: Map.Entry<String, Step>,
    trace: String, // A
    steps: Map<String, Step>,
    range: PartRange,
): List<PartRange> {
    val (currentName, currentStep) = current

    val allAccept = currentStep.checks.all { it.outcome == trace }

    fun findAllWhoCalls(name: String): Map<String, Step> {
        return steps.filterValues { step -> step.canCall(name) }
    }

    if (allAccept) {
        "all accept $current $range".log()
        return findAllWhoCalls(currentName)
            .entries
            .flatMap {
                findRange(
                    current = it,
                    trace = currentName,
                    steps = steps,
                    range = range
                )
            }
    } else {
        val reversed = currentStep.checks.reversed()
            .dropWhile { it.outcome != trace }
        val ranges = mutableListOf<PartRange>()
        "purging $current $range $reversed".log()
        for (i in reversed.indices) {
            val check = reversed[i]
            if (check.outcome == trace) {
                var newRange = newRange(check, range)
                "CHECK $check new range after initial $newRange".log()
                if (i > 0) {
                    for (j in i - 1 downTo 0) {
                        val invertCheck = reversed[j]
                        newRange = newRange(invertCheck, newRange, invert = true)
                    }
                }
                "CHECK $check new range after purge $newRange".log()
                ranges.add(newRange)
            }
        }
        if (currentName == "in") {
            return ranges
        }
        return findAllWhoCalls(currentName)
            .entries
            .flatMap {
                ranges.flatMap { range ->
                    findRange(
                        current = it,
                        trace = currentName,
                        steps = steps,
                        range = range,
                    )
                }
            }
    }
}

private fun newRange(check: Step.ICheck, range: PartRange, invert: Boolean = false): PartRange {
    return when (check) {
        is Step.Default -> range
        is Step.Check -> {
            val n = check.n
            val symbol = if (invert) {
                if (check.symbol == '<') '>' else '<'
            } else {
                check.symbol
            }
            when (symbol) {
                '<' -> {
                    val newStart = n - if (invert) 0 else 1
                    when (check.field) {
                        'x' -> range.copy(x = range.x.first..min(newStart, range.x.last))
                        'm' -> range.copy(m = range.m.first..min(newStart, range.m.last))
                        'a' -> range.copy(a = range.a.first..min(newStart, range.a.last))
                        's' -> range.copy(s = range.s.first..min(newStart, range.s.last))
                        else -> error("")
                    }
                }

                '>' -> {
                    val newEnd = n + if (invert) 0 else 1
                    when (check.field) {
                        'x' -> range.copy(x = max(range.x.first, newEnd)..range.x.last)
                        'm' -> range.copy(m = max(range.m.first, newEnd)..range.m.last)
                        'a' -> range.copy(a = max(range.a.first, newEnd)..range.a.last)
                        's' -> range.copy(s = max(range.s.first, newEnd)..range.s.last)
                        else -> error("")
                    }
                }

                else -> error("")
            }
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
    val checks = split.dropLast(1).map { Check(it) } + Default(default)

    fun canCall(name: String): Boolean = default == name || checks.any { it.outcome == name }

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

private fun tryReducSteps() {
//
//    var currentSteps = steps
//    currentSteps.log("before:")
//    currentSteps.size.log("before size:")
//    while (true) {
//        currentSteps.size.log("steps:")
//        val instaWins = currentSteps.filter { it.value.betterChecks.all { it.second == "A" } && it.value.default == "A" }
//            .log("Could be replaced by A:")
//            .also { "win: ${it.size}".log() }
//        val instaLoss = currentSteps.filter { it.value.betterChecks.all { it.second == "R" } && it.value.default == "R" }
//            .log("Could be replaced by R:")
//            .also { "loss: ${it.size}".log() }
//        println()
//        val newSteps = currentSteps
//            .filter { it.key !in instaWins.keys }
//            .filter { it.key !in instaLoss.keys }
//            .mapValues { (name, step) ->
//                var stepInput = step.input
//                instaWins.keys.forEach { toBeReplaced ->
//                    stepInput = stepInput.replace(toBeReplaced, "A")
//                }
//                instaLoss.keys.forEach { toBeReplaced ->
//                    stepInput = stepInput.replace(toBeReplaced, "R")
//                }
//                Step(stepInput)
//            }
//        if (newSteps == currentSteps) break
//        currentSteps = newSteps
//    }
//
//    currentSteps.log("after:")
//    currentSteps.size.log("after size:")
//    steps = currentSteps
}