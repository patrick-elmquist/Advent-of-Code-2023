package common.util

enum class Direction { Left, Up, Right, Down }

val Direction.opposite
    get() = when (this) {
        Direction.Left -> Direction.Right
        Direction.Up -> Direction.Down
        Direction.Right -> Direction.Left
        Direction.Down -> Direction.Up
    }

val Direction.isHorizontal
    get() = this == Direction.Left || this == Direction.Right

val Direction.isVertical
    get() = this == Direction.Up || this == Direction.Down

val Direction.nextCW: Direction
    get() = when (this) {
        Direction.Left -> Direction.Up
        Direction.Up -> Direction.Right
        Direction.Right -> Direction.Down
        Direction.Down -> Direction.Left
    }

val Direction.nextCCW: Direction
    get() = when (this) {
        Direction.Left -> Direction.Down
        Direction.Up -> Direction.Left
        Direction.Right -> Direction.Up
        Direction.Down -> Direction.Right
    }
