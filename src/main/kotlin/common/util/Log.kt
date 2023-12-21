@file:Suppress("NOTHING_TO_INLINE", "unused")

package common.util

import kotlinx.coroutines.flow.merge
import kotlin.math.max
import kotlin.math.min

/**
 * Abuse the not operator fun for quick logging
 * If you are reading this, for the love of god, don't use this in production code
 * Example : !"Log this"
 */
operator fun String.not() = println(this)

var loggingEnabled = true
var tagSize = 0

inline fun <T> T.log(): T {
    if (!loggingEnabled) return this
    return this.also { println(it) }
}

inline fun <T> T.log(msg: () -> Any): T {
    if (!loggingEnabled) return this
    return this.also { println(msg()) }
}

inline infix fun <T> T.log(msg: Any): T {
    if (!loggingEnabled) return this
    val tag = msg.toString()
    tagSize = max(tagSize, tag.length)
    return this.also { println("${tag.padStart(tagSize, ' ')}\t$it") }
}
