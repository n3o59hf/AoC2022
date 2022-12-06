package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task


class T06(input: Input) : Task(input) {
    private val data = input.raw().trim()

    private fun findHeaderPosition(length: Int) = length + data
        .windowed(length, 1, partialWindows = true)
        .withIndex()
        .first{ (_, packet) -> packet.toSet().size == length}
        .index

    override fun a() = findHeaderPosition(4)

    override fun b()= findHeaderPosition(14)

}

