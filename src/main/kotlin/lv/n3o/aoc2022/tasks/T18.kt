package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C3


class T18(input: Input) : Task(input) {
    private val data = input.asLines().map {
        val (a,b,c) = it.split(",").map(String::toInt)
        C3(a,b,c)
    }.toSet()

    override fun a() = data.sumOf { c ->
        c.neighbors6.count { !data.contains(it) }
    }

    override fun b(): Int {
        val potentialVexels = data.flatMap { it.neighbors27(false) }.toSet() - data
        val cornerPiece = potentialVexels.minOf { it }
        val toCheck = mutableSetOf(cornerPiece)
        val crust = mutableSetOf(cornerPiece)

        while(toCheck.isNotEmpty()) {
            val current = toCheck.first()
            toCheck.remove(current)
            val neighbors = current.neighbors6
            val newCrust = neighbors.filter { it in potentialVexels }
            toCheck.addAll(newCrust - crust)
            crust.addAll(newCrust)
        }
        return data.sumOf { c ->
            c.neighbors6.count { !data.contains(it) && crust.contains(it) }
        }
    }
}