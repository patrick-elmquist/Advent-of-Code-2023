@file:Suppress("NOTHING_TO_INLINE")

package common

import kotlinx.coroutines.runBlocking
import kotlin.time.measureTimedValue

typealias Solver = (Input) -> Any?

fun day(
    n: Int,
    block: Sheet.() -> Unit
) = runBlocking {
    if (makeSureInputFileIsAvailable(day = n)) {
        collectSolutions(n, block).verifyAndRun(input = Input(day = n))
    } else {
        println("Input file is not available")
    }
}

private inline fun collectSolutions(
    day: Int,
    block: Sheet.() -> Unit
) = Sheet(day = day).apply(block)

private inline fun Sheet.verifyAndRun(input: Input) {
    println("Day $day")
    val hasTests = parts.any { it.config.tests.isNotEmpty() }
    parts.forEachIndexed { i, (solution, config) ->
        val n = i + 1

        if (config.ignore) {
            println("[IGNORING] Part $n")
            return@forEachIndexed
        }

        val result = solution.evaluate(
            n = n,
            input = input,
            expected = config.expected,
            testOnly = config.breakAfterTest,
            tests = config.tests,
        )
        print("answer #$n: ")
        result
            .onSuccess {
                println("${it.output} (${it.time.inWholeMilliseconds}ms)")
            }
            .onFailure {
                println(it.message)
            }
        if (hasTests) println()
    }
}

private inline fun Solver.evaluate(
    n: Int,
    input: Input,
    expected: Any?,
    testOnly: Boolean,
    tests: List<Test>
): Result<Answer> {
    if (tests.isNotEmpty()) println("Verifying Part #$n")

    val testsPassed = tests.all {
        val testInput = it.input
        val result = runWithTimer(testInput)
        val testPassed = result.output == it.expected

        print("[${if (testPassed) "PASS" else "FAIL"}]")
        print(" Input: ${testInput.lines}")
        println()
        if (!testPassed) {
            println("Expected: ${it.expected}")
            println("Actual: ${result.output}")
        }

        testPassed
    }

    if (!testsPassed) return failure("One or more tests failed.")

    if (testOnly) return failure("Break added")

    return try {
        val result = runWithTimer(input)
        if (expected == null || result.output == expected) {
            success(result)
        } else {
            failure("FAIL Expected:$expected actual:${result.output}")
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        failure(e)
    }
}

private inline fun Solver.runWithTimer(input: Input) =
    measureTimedValue { invoke(input) }
        .let { result -> Answer(result.value, result.duration) }

private inline fun success(answer: Answer) = Result.success(answer)
private inline fun failure(message: String) = Result.failure<Answer>(AssertionError(message))
private inline fun failure(throwable: Throwable) = Result.failure<Answer>(throwable)
