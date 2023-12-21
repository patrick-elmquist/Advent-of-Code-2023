package common

import common.util.Point
import common.util.pointCharMap
import java.io.File

data class Input(val lines: List<String>) {
    constructor(file: File) : this(file.readLines())
    constructor(day: Int) : this(inputFileFor(day = day))
    constructor(day: Int, test: Int) : this(testFileFor(day = day, n = test))

    companion object
}

val Input.pointCharMap: Map<Point, Char>
    get() = lines.pointCharMap

val Input.bounds: Pair<Int, Int>
    get() = lines.first().length to lines.size
