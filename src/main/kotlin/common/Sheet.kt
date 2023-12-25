package common

class Sheet(val day: Int) {
    private lateinit var solver1: Solver
    private lateinit var config1: Config

    private lateinit var solver2: Solver
    private lateinit var config2: Config

    val parts: List<Part>
        get() = listOf(Part(solver1, config1), Part(solver2, config2))

    fun part1(block: (Input) -> Any?) {
        solver1 = block
    }

    fun part2(block: (Input) -> Any?) {
        solver2 = block
    }

    fun verify(block: ConfigBuilder.() -> Unit) {
        val builder = ConfigBuilder(day).apply(block)
        require(::solver1.isInitialized)
        if (::config1.isInitialized) {
            require(::solver2.isInitialized && !::config2.isInitialized)
            config2 = builder.build()
        } else {
            config1 = builder.build()
        }
    }
}

class ConfigBuilder(private val day: Int) {
    val run: TestBuilder get() = TestBuilder()
    val expect: ConfigBuilder get() = this

    private var ignore: Boolean = false
    private var breakAfterTest: Boolean = false
    private var expectedResult: Any? = null

    private val tests = mutableListOf<Test>()

    infix fun ConfigBuilder.result(result: Any?) =
        apply { expectedResult = result }

    infix fun TestBuilder.test(test: Int): TestBuilder =
        apply { input = Input(day = day, test = test) }

    infix fun TestBuilder.test(lines: List<String>): TestBuilder =
        apply { input = Input(lines) }

    infix fun TestBuilder.expect(expected: Any?) {
        tests += apply { expect = expected }.build()
    }

    class TestBuilder {
        var input: Input? = null
        var expect: Any? = null
        fun build() = Test(requireNotNull(input), expect)
    }

    fun build() = Config(
        expected = expectedResult,
        tests = tests.toList(),
        ignore = ignore,
        breakAfterTest = breakAfterTest
    )
}
