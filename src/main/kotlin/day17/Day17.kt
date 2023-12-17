package day17

import common.day
import common.pointCharMap
import common.util.*
import java.util.*

// answer #1: 755
// answer #2: 881

fun main() {
    day(n = 17) {
        part1 { input ->
            val city = input.pointCharMap.mapValues { it.value.digitToInt() }
            val end = Point(x = city.maxOf { it.key.x }, y = city.maxOf { it.key.y })
            val visited = mutableSetOf<Triple<Point, Direction, Int>>()
            val queue = initPriorityQueue(city)

            while (queue.isNotEmpty()) {
                val node = queue.poll()

                if (node.point == end) return@part1 node.heatLoss
                if (node.visitKey !in visited) {
                    visited += node.visitKey
                    queue += listOfNotNull(
                        node.direction.takeIf { node.steps < 3 },
                        node.direction.nextCW,
                        node.direction.nextCCW,
                    ).mapNotNull { dir ->
                        node.point.nextInDirection(dir)
                            .takeIf { point -> point in city }
                            ?.let { point ->
                                Node(
                                    point = point,
                                    direction = dir,
                                    steps = if (dir == node.direction) node.steps + 1 else 1,
                                    heatLoss = node.heatLoss + city.getValue(point)
                                )
                            }
                    }
                }
            }
        }
        verify {
            expect result 755
            run test 2 expect 7
            run test 3 expect 5
            run test 1 expect 102
        }

        part2 { input ->
            val city = input.pointCharMap.mapValues { it.value.digitToInt() }
            val end = Point(x = city.maxOf { it.key.x }, y = city.maxOf { it.key.y })
            val visited = mutableSetOf<Triple<Point, Direction, Int>>()
            val queue = initPriorityQueue(city)

            while (queue.isNotEmpty()) {
                val node = queue.poll()

                if (node.point == end) return@part2 node.heatLoss
                if (node.visitKey in visited) continue
                visited += node.visitKey

                if (node.steps in 0..9) {
                    queue += listOf(node.point.nextInDirection(node.direction))
                        .filter { it in city }
                        .map { point ->
                            Node(
                                point = point,
                                direction = node.direction,
                                steps = node.steps + 1,
                                heatLoss = node.heatLoss + city.getValue(point)
                            )
                        }
                }
                if (node.steps > 3) {
                    queue += listOf(node.direction.nextCW, node.direction.nextCCW)
                        .filter { dir -> node.point.nextInDirection(dir, steps = 4) in city }
                        .map { dir ->
                            val newPoint = node.point.nextInDirection(dir)
                            Node(
                                point = newPoint,
                                direction = dir,
                                steps = 1,
                                heatLoss = node.heatLoss + city.getValue(newPoint)
                            )
                        }
                }
            }
        }
        verify {
            expect result 881
            run test 1 expect 94
            run test 4 expect 71
        }
    }
}

private data class Node(
    val point: Point,
    val direction: Direction,
    val steps: Int,
    val heatLoss: Int,
) {
    val visitKey = Triple(point, direction, steps)
}

private fun initPriorityQueue(map: Map<Point, Int>): PriorityQueue<Node> {
    val start = Point(x = 0, y = 0)
    return PriorityQueue(compareBy(Node::heatLoss)).apply {
        add(
            Node(
                point = start.rightNeighbour,
                direction = Direction.Right,
                steps = 1,
                heatLoss = map.getValue(start.rightNeighbour),
            ),
        )
        add(
            Node(
                point = start.belowNeighbour,
                direction = Direction.Down,
                steps = 1,
                heatLoss = map.getValue(start.belowNeighbour),
            ),
        )
    }
}
