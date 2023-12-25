package day24

import common.day
import common.util.PointL3D
import common.util.eq
import common.util.minus
import common.util.plus
import common.util.times
import common.util.z3

// answer #1: 14799
// answer #2: 1007148211789625

fun main() {
    day(n = 24) {
        part1 { input ->
            val hail = input.lines.map {
                it.split(" @ ")
                    .map { it.split(", ").map { it.trim() } }.map { (x, y, z) -> PointL3D(x, y, z) }.let {
                    it.first() to it.last()
                }
            }
            val range = if (hail.size == 5) {
                7L..27L
            } else {
                200000000000000L..400000000000000L
            }

            var counter = 0
            for (a in hail) {
                for (b in hail) {
                    val intersectInArea = intersectInArea(a, b, range, range)
                    if (intersectInArea) {
                        counter++
                    }
                }
            }
            counter / 2
        }
        verify {
            expect result 14799
            run test 1 expect 2
        }

        part2 { input ->
            val hail = input.lines.map {
                it.split(" @ ")
                    .map { it.split(", ").map { it.trim() } }.map { (x, y, z) -> PointL3D(x, y, z) }.let {
                        Hail(it.first(), it.last())
                    }
            }
            z3 {
                val x_t = int("x_t")
                val y_t = int("y_t")
                val z_t = int("z_t")
                val xvel_t = int("xvel_t")
                val yvel_t = int("yvel_t")
                val zvel_t = int("zvel_t")
                val dt1 = int("dt1")
                val dt2 = int("dt2")
                val dt3 = int("dt3")

                val dt = listOf(dt1, dt2, dt3)

                val eqs = hail.take(3).flatMapIndexed { idx, ball ->
                    listOf(
                        (x_t - ball.position.x) eq (dt[idx] * (ball.velocity.x - xvel_t)),
                        (y_t - ball.position.y) eq (dt[idx] * (ball.velocity.y - yvel_t)),
                        (z_t - ball.position.z) eq (dt[idx] * (ball.velocity.z - zvel_t)),
                    )
                }

                solve(eqs)

                eval(x_t + y_t + z_t).toLong()
            }
        }
        verify {
            expect result 1007148211789625L
        }
    }
}

private data class Hail(
    val position: PointL3D,
    val velocity: PointL3D,
)

private fun intersectInArea(
    a: Pair<PointL3D, PointL3D>,
    b: Pair<PointL3D, PointL3D>,
    xRange: LongRange,
    yRange: LongRange,
): Boolean {
    val (x1, y1, _) = a.first
    val (x2, y2, _) = b.first

    val (aPos, aVel) = a
    val aEnd = aPos + aVel

    val (bPos, bVel) = b
    val bEnd = bPos + bVel

    val m1 = (aEnd.y - aPos.y) / (aEnd.x - aPos.x).toFloat()
    val m2 = (bEnd.y - bPos.y) / (bEnd.x - bPos.x).toFloat()

    if (m1 == m2) {
        return false
    }

    val b1 = y1 - m1 * x1
    val b2 = y2 - m2 * x2

    val x = ((b2 - b1) / (m1 - m2)).toLong()
    val y = (m1 * x + b1).toLong()

    val ta = (x - aPos.x) / aVel.x
    val tb = (x - bPos.x) / bVel.x
    if (ta < 0 || tb < 0) return false

    return x in xRange && y in yRange
}