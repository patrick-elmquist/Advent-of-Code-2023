package common

import common.util.Point
import common.util.grid
import java.io.File

data class Input(val lines: List<String>) {
    constructor(file: File) : this(file.readLines())
    constructor(day: Int) : this(inputFileFor(day = day))
    constructor(day: Int, test: Int) : this(testFileFor(day = day, n = test))

    companion object
}

val Input.grid: Map<Point, Char>
    get() = lines.grid

val Input.bounds: Pair<Int, Int>
    get() = lines.first().length to lines.size
