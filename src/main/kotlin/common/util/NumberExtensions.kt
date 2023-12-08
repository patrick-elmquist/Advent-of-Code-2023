package common.util

fun main() {
    // isBitSet tests
    0b101.isBitSet(0) assert true
    0b101.isBitSet(1) assert false
    0b101.isBitSet(2) assert true

    println("Test OK")
}

fun Int.isBitSet(index: Int): Boolean = (this shr index) and 1 != 0

fun Int.getBit(index: Int): Int = if (isBitSet(index)) 1 else 0

fun leastCommonMultiple(a: Int, b: Int): Int {
    return a * b / greatestCommonDivisor(a, b)
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * b / greatestCommonDivisor(a, b)
}

tailrec fun greatestCommonDivisor(a: Int, b: Int): Int {
    return if (b == 0) a
    else greatestCommonDivisor(b, a % b)
}

tailrec fun greatestCommonDivisor(a: Long, b: Long): Long {
    return if (b == 0L) a
    else greatestCommonDivisor(b, a % b)
}

private infix fun <T> T.assert(expected: T) = assert(this == expected) { "Assert failed: $this != $expected" }
