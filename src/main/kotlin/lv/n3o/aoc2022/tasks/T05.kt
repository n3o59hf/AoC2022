package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.transpose

class T05(input: Input) : Task(input) {
    private val data = input.asBlocks()
    private val stacks = data.first().asLines(false).map { it.chunked(4) }.transpose("").map { crates ->
        crates.map { crateId -> crateId.filter(Char::isLetter) }.filter(String::isNotBlank).reversed()
    }
    private val instructions = data.last().asLines().map {
        it.split(" ").mapNotNull(String::toIntOrNull).let { (c, f, t) -> Instruction(c, f - 1, t - 1) }
    }

    override fun a(): String {
        val workStacks = stacks.map { it.toMutableList() }
        for ((count, from, to) in instructions) {
            repeat(count) { workStacks[to].add(workStacks[from].removeLast()) }
        }
        return workStacks.joinToString("") { it.last() }
    }

    override fun b(): String {
        val workStacks = stacks.map { it.toMutableList() }
        for ((count, from, to) in instructions) {
            workStacks[to].addAll(workStacks[from].takeLast(count))
            repeat(count) { workStacks[from].removeLast() }
        }
        return workStacks.joinToString("") { it.last() }
    }

    private data class Instruction(
        val count: Int, val from: Int, val to: Int
    )
}