package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task


class T01(input: Input) : Task(input) {
    private val data = input.asLinesPerBlock().map { l -> l.map(String::toLong) }

    override fun a() = data.maxOf { it.sum() }

    override fun b()= data.map { it.sum() }.sortedDescending().take(3).sum()
}