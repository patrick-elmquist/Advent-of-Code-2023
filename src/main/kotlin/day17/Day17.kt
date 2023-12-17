package day17

import common.day
import common.pointCharMap
import common.util.*
import java.util.*
import kotlin.math.abs

// answer #1:
// answer #2:

fun main() {
    day(n = 17) {
        part1 { input ->
            val lines = input.lines
            val map = input.pointCharMap.mapValues { it.value.digitToInt() }

            val start = Point(x = 0, y = 0)
            val end = Point(x = lines.first().lastIndex, y = lines.lastIndex).log()

            data class Node(
                val point: Point,
                val direction: Direction,
                val steps: Int,
            )

            val visited = mutableSetOf(Triple(start, Direction.Right, 0))
            val distance = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
            distance[start] = 0
            distance[start.rightNeighbour] = map.getValue(start.rightNeighbour)
            distance[start.belowNeighbour] = map.getValue(start.belowNeighbour)
            val nodes = PriorityQueue<Node>(compareBy { distance})
            val prev = mutableMapOf<Point, Point>()
            prev[start.rightNeighbour] = start
            prev[start.belowNeighbour] = start
            nodes.apply {
                add(Node(start.rightNeighbour, Direction.Right, 1))
                add(Node(start.belowNeighbour, Direction.Down, 1))
            }

            while (nodes.isNotEmpty()) {
                val node = nodes.minBy { distance.getValue(it.point) }
                nodes.remove(node)
                println("PROCESSING: ${node.point} dir:${node.direction}")
                val loss = distance.getValue(node.point)

                visited += Triple(node.point, node.direction, node.steps)

                val potentialSteps = buildList {
                    if (node.steps < 3) {
                        "adding forward ${node.point.neighborInDirection(node.direction)} ${node.direction} ${node.steps}".log()
                        add(
                            Node(
                                point = node.point.neighborInDirection(node.direction),
                                direction = node.direction,
                                steps = node.steps + 1,
                            )
                        )
                    }

                    val leftDir = node.direction.nextCCW
                    val leftPoint = node.point.neighborInDirection(leftDir)
                    add(
                        Node(
                            point = leftPoint,
                            direction = leftDir,
                            steps = 1,
                        )
                    )

                    val rightDir = node.direction.nextCW
                    val rightPoint = node.point.neighborInDirection(rightDir)
                    add(
                        Node(
                            point = rightPoint,
                            direction = rightDir,
                            steps = 1,
                        )
                    )
                }

                potentialSteps.forEach { next ->
                    when {
                        next.point !in map -> {}
                        Triple(next.point, next.direction, next.steps) !in visited -> {
                            val newLoss = loss + map.getValue(next.point)
                            val existingLoss = distance.getValue(next.point)
                            if (newLoss < existingLoss) {
                                "queuing $next with loss $newLoss".log()
                                distance[next.point] = newLoss
                                nodes.add(next)
                                prev[next.point] = node.point
                            } else {
                                "ignoring $next with newloss $newLoss >= $existingLoss".log()
                            }
                        }

                        else -> {
                            "${next.point} was in visited"
                        }
                    }
                }
            }
            distance[end].log("end ")
            var current = end
            val path = mutableListOf<Point>()
            while (current != start) {
                path += current
                current = prev.getValue(current)
            }
            path.reversed().log("path:")
            printMap(map, path.toSet())
            println()
            val minMaxX = map.minOf { it.key.x }..map.maxOf { it.key.x }
            val minMaxY = map.minOf { it.key.y }..map.maxOf { it.key.y }
            for (y in minMaxY) {
                for (x in minMaxX) {
                    val p = Point(x, y)
                    val distance = distance.getValue(p)
                    if (distance < 10) {
                        print(" $distance ")
                    } else {
                        print("$distance ")
                    }
                }
                println()
            }

            distance[end]
        }
        verify {
            expect result null
            run test 2 expect 7
            run test 3 expect 5
            run test 1 expect 102
        }

        part2 { input ->

        }
        verify {
            expect result null
            run test 1 expect Unit
        }
    }
}

private fun initialTry(end: Point, map: Map<Point, Int>): Int {
    data class Node(
        val p: Point,
        val direction: Direction,
        val stepsInDirection: Int,
        val heatLost: Int,
        val visitedNodes: Set<Point>,
        val path: List<Pair<Point, Direction>> = listOf(p to direction),
    )

    val ends = mutableListOf<Int>()
    val visited = mutableMapOf<Point, Int>()
    val nodes = PriorityQueue<Node> { o1, o2 ->
        o1.heatLost.compareTo(o2.heatLost)
    }
    nodes.add(
        Node(
            p = Point(1, 0),
            direction = Direction.Right,
            heatLost = 0,
            stepsInDirection = 1,
            visitedNodes = emptySet(),
        )
    )

    while (nodes.isNotEmpty()) {
        val node = nodes.remove()
//                nodes.size.log("node ${node.p} dir:${node.direction} stepsInDir:${node.stepsInDirection} loss:${node.heatLost} size:")
        nodes.size.log("size: ")

        val loss = map.getValue(node.p)
        val heatLost = node.heatLost + loss
        if (node.p == end) {
//            printMap(map, node.path)
            println()
            println("end $heatLost")
            ends += heatLost
            break
        }

        visited[node.p] = heatLost

        if (node.stepsInDirection < 3) {
            val nextPoint = node.p.neighborInDirection(node.direction)
            if (nextPoint in map) {
                val allowed = nextPoint !in node.visitedNodes
                if (allowed) {
                    nodes.add(
                        Node(
                            p = nextPoint,
                            direction = node.direction,
                            stepsInDirection = node.stepsInDirection + 1,
                            heatLost = heatLost,
                            path = node.path + (nextPoint to node.direction),
                            visitedNodes = node.visitedNodes + node.p,
                        )
                    )
                }
            }
        }

        // add left
        val leftDirection = node.direction.nextCCW
        val leftPoint = node.p.neighborInDirection(leftDirection)
        if (leftPoint in map) {
            val allowed = leftPoint !in node.visitedNodes
            if (allowed) {
                nodes.add(
                    Node(
                        p = leftPoint,
                        direction = leftDirection,
                        stepsInDirection = 1,
                        heatLost = heatLost,
                        path = node.path + (leftPoint to leftDirection),
                        visitedNodes = node.visitedNodes + node.p,
                    )
                )
            }
        }

        // add right
        val rightDirection = node.direction.nextCW
        val rightPoint = node.p.neighborInDirection(rightDirection)
        if (rightPoint in map) {
            val allowed = rightPoint !in node.visitedNodes
            if (allowed) {
                nodes.add(
                    Node(
                        p = rightPoint,
                        direction = rightDirection,
                        stepsInDirection = 1,
                        heatLost = heatLost,
                        path = node.path + (rightPoint to rightDirection),
                        visitedNodes = node.visitedNodes + node.p,
                    )
                )
            }
        }
    }
    return ends.first()
}

private fun Point.distance(other: Point): Int =
    abs(other.x - x) + abs(other.y - y)

private fun printMap(map: Map<Point, Int>, path: Set<Point>) {
    val minMaxX = map.minOf { it.key.x }..map.maxOf { it.key.x }
    val minMaxY = map.minOf { it.key.y }..map.maxOf { it.key.y }

    for (y in minMaxY) {
        for (x in minMaxX) {
            val p = Point(x, y)
            if (p in path) {
                print('*')
            } else {
                print(map[p])
            }
        }
        println()
    }
}