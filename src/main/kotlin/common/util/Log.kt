@file:Suppress("NOTHING_TO_INLINE", "unused")

package common.util

var loggingEnabled = true

/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example : !"Log this"
 */
inline operator fun String.not() {
    if (loggingEnabled) println(this)
}

inline fun <T> T.log(): T {
    if (loggingEnabled) println(this)
    return this
}

inline infix fun <T> T.log(msg: Any): T {
    if (loggingEnabled)  println("$msg $this")
    return this
}
