package day17

import common.day
import common.pointCharMap
import common.util.*
import java.util.*

// answer #1: 755
// answer #2:

data class Node(
    val point: Point,
    val direction: Direction,
    val steps: Int,
    val heatLoss: Int,
)

fun main() {
    day(n = 17) {
        part1 { input ->
            val lines = input.lines
            val map = input.pointCharMap.mapValues { it.value.digitToInt() }

            val start = Point(x = 0, y = 0)
            val end = Point(x = lines.first().lastIndex, y = lines.lastIndex).log()

            val visited = mutableSetOf<Triple<Point, Direction, Int>>()
            val queue = PriorityQueue<Node>(compareBy { it.heatLoss })
            queue.add(
                Node(
                    point = start.rightNeighbour,
                    direction = Direction.Right,
                    steps = 1,
                    heatLoss = map.getValue(start.rightNeighbour),
                ),
            )
            queue.add(
                Node(
                    point = start.belowNeighbour,
                    direction = Direction.Down,
                    steps = 1,
                    heatLoss = map.getValue(start.belowNeighbour),
                ),
            )

            while (queue.isNotEmpty()) {
                val node = queue.poll()

                if (node.point == end) {
                    return@part1 node.heatLoss
                }

                val key = Triple(node.point, node.direction, node.steps)
                if (key in visited) {
                    continue
                }

                visited += key
                if (node.steps < 3) {
                    val nextPoint = node.point.neighborInDirection(node.direction)
                    if (nextPoint in map) {
                        queue.add(
                            Node(
                                point = nextPoint,
                                direction = node.direction,
                                steps = node.steps + 1,
                                heatLoss = node.heatLoss + map.getValue(nextPoint)
                            )
                        )
                    }
                }
                listOf(node.direction.nextCW, node.direction.nextCCW).forEach { newDir ->
                    val newPoint = node.point.neighborInDirection(newDir)
                    if (newPoint in map) {
                        queue.add(
                            Node(
                                point = newPoint,
                                direction = newDir,
                                steps = 1,
                                heatLoss = node.heatLoss + map.getValue(newPoint)
                            )
                        )
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
            val lines = input.lines
            val map = input.pointCharMap.mapValues { it.value.digitToInt() }

            val start = Point(x = 0, y = 0)
            val end = Point(x = lines.first().lastIndex, y = lines.lastIndex).log()

            val visited = mutableSetOf<Triple<Point, Direction, Int>>()
            val queue = PriorityQueue<Node>(compareBy { it.heatLoss })
            queue.add(
                Node(
                    point = start.rightNeighbour,
                    direction = Direction.Right,
                    steps = 1,
                    heatLoss = map.getValue(start.rightNeighbour),
                ),
            )
            queue.add(
                Node(
                    point = start.belowNeighbour,
                    direction = Direction.Down,
                    steps = 1,
                    heatLoss = map.getValue(start.belowNeighbour),
                ),
            )

            while (queue.isNotEmpty()) {
                val node = queue.poll()

                if (node.point == end) {
                    return@part2 node.heatLoss
                }

                val key = Triple(node.point, node.direction, node.steps)
                if (key in visited) {
                    continue
                }

                visited += key
                if (node.steps < 3) {
                    val nextPoint = node.point.neighborInDirection(node.direction)
                    if (nextPoint in map) {
                        queue.add(
                            Node(
                                point = nextPoint,
                                direction = node.direction,
                                steps = node.steps + 1,
                                heatLoss = node.heatLoss + map.getValue(nextPoint)
                            )
                        )
                    }
                }
                listOf(node.direction.nextCW, node.direction.nextCCW).forEach { newDir ->
                    val newPoint = node.point.neighborInDirection(newDir)
                    if (newPoint in map) {
                        queue.add(
                            Node(
                                point = newPoint,
                                direction = newDir,
                                steps = 1,
                                heatLoss = node.heatLoss + map.getValue(newPoint)
                            )
                        )
                    }
                }
            }
        }
        verify {
            expect result null
            run test 1 expect 94
            run test 4 expect 71
        }
    }
}
