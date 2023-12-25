package common

import kotlin.time.Duration

data class Part(
    val algorithm: (Input) -> Any?,
    val config: Config,
)

data class Config(
    val expected: Any?,
    val tests: List<Test>,
    val ignore: Boolean,
    val breakAfterTest: Boolean,
)

data class Test(
    val input: Input,
    val expected: Any?
)

data class Answer(
    val output: Any?,
    val time: Duration
)
