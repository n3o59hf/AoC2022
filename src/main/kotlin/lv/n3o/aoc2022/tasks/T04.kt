package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task


class T04(input: Input) : Task(input) {
    private val data = input.asLines().map { l ->
            l.split(",").map { p -> p.split("-").map { it.toInt() }.let { (a, b) -> a..b } }
        }

    override fun a() = data.count { (a, b) -> (a - b).isEmpty() or (b - a).isEmpty() }

    override fun b() = data.count { (a, b) -> a.intersect(b).isNotEmpty() }
}