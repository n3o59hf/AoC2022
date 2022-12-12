package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2

class T12(input: Input) : Task(input) {
    private val data = input.asCoordGrid()

    private fun shortestPathFrom(vararg startPoints: Char): Int {
        val queue = data.entries.filter { it.value in startPoints }.map { it.key to 0 }.toMutableList()
        val end = data.entries.first { it.value == 'E' }.key
        val visited = mutableSetOf<C2>()

        while (queue.first().first != end) {
            val (next, steps) = queue.removeFirst()

            if (!visited.add(next)) continue
            val allowedHeight = (data[next] ?: error("Out of bounds")).height + 1

            next.neighbors4()
                .filter {
                    if (it in visited) false else {
                        data[it]?.let { c -> c.height <= allowedHeight } ?: false
                    }
                }
                .let { queue.addAll(it.map { c -> c to steps + 1 }) }
        }
        return queue.first().second
    }

    override fun a() = shortestPathFrom('S')

    override fun b() = shortestPathFrom('S', 'a')

    private val Char.height: Int
        get() = when (this) {
            'S' -> 'a'.height
            'E' -> 'z'.height
            else -> this.code - 'a'.code + 1
        }
}