package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2


class T09(input: Input) : Task(input) {
    private val data = input.asLines().map {
        val (dir, len) = it.split(" ")
        val dirV = when (dir) {
            "D" -> C2(0, 1)
            "U" -> C2(0, -1)
            "L" -> C2(-1, 0)
            "R" -> C2(1, 0)
            else -> error("Unknown direction $dir")
        }
        dirV to len.toInt()
    }

    private fun traceRopeTail(ropeLenght: Int): Int {
        val rope = (1..ropeLenght).map { C2(0, 0) }.toTypedArray()
        val visited = mutableListOf(rope.last())
        for ((dir, len) in data) {
            for (s in 1..len) {
                rope[0] = rope[0] + dir
                for (i in 1 until ropeLenght) {
                    val h = rope[i - 1]
                    val t = rope[i]
                    if (!h.isNeighbor8(t)) {
                        rope[i] = t + t.vector(h).unit()
                    }
                }
                visited += rope.last()
            }
        }
        return visited.toSet().size
    }

    override fun a() = traceRopeTail(2)

    override fun b(): Int = traceRopeTail(10)

}