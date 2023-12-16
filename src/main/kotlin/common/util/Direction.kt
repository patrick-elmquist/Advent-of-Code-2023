package common.util

enum class Direction { Left, Up, Right, Down }

val Direction.isHorizontal
    get() = this == Direction.Left || this == Direction.Right

val Direction.isVertical
    get() = this == Direction.Up || this == Direction.Down

