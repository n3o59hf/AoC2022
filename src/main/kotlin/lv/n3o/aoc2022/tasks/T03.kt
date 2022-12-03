package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.intersectAll


class T03(input: Input) : Task(input) {
    private val data = input.asLines().map { it.toCharArray().toList() }
    override fun a() = data.sumOf { it.chunked(it.size / 2).intersectAll().first().priority }

    override fun b() = data.map(List<Char>::toSet).chunked(3).sumOf { it.intersectAll().first().priority }
}

private val Char.priority get() = if (isLowerCase()) this - 'a' + 1 else this - 'A' + 27
