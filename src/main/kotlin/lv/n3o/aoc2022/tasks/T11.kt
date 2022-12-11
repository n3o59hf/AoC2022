package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task

class T11(input: Input) : Task(input) {
    private val data = input.asLinesPerBlock().map { lines ->
        Monkey(
            lines[1].split(": ").last().split(", ").map { it.toLong() },
            lines[2].split(" = ").last().split(" ").let { (arg1, op, arg2) ->
                { old ->
                    val a1 = if (arg1 == "old") old else arg1.toLong()
                    val a2 = if (arg2 == "old") old else arg2.toLong()
                    when (op) {
                        "+" -> a1 + a2
                        "*" -> a1 * a2
                        else -> error("Unknown op $op")
                    }
                }
            },
            lines[3].split(" ").last().toLong(),
            lines[4].split(" ").last().toInt(),
            lines[5].split(" ").last().toInt()
        )
    }

    private fun monkeyBusiness(rounds: Int, reducer: Long): Long {
        val divider = data.fold(reducer) { acc, monkey -> acc * monkey.testDiv }
        val monkeyStuff = data.map { it.copy() }
        repeat(rounds) {
            for (monkey in monkeyStuff) {
                monkey.inspectAndThrow(reducer, divider).forEach { (i, t) -> monkeyStuff[i].items += t }
            }
        }
        return monkeyStuff.map { it.inspectCount }.sorted().takeLast(2).reduce { a, b -> a * b }
    }

    override fun a() = monkeyBusiness(20, 3)

    override fun b() = monkeyBusiness(10000, 1)

    data class Monkey(
        var items: List<Long>,
        val operation: (Long) -> Long,
        val testDiv: Long,
        val ifTrue: Int,
        val ifFalse: Int
    ) {
        var inspectCount: Long = 0

        fun inspectAndThrow(reducer: Long, divider: Long): List<Pair<Int, Long>> {
            inspectCount += items.count()
            val toThrow = items.map {
                val newWorry = (operation(it) / reducer) % divider
                val whoToThrow = if (newWorry % testDiv == 0L) ifTrue else ifFalse
                whoToThrow to newWorry
            }
            items = listOf()
            return toThrow
        }
    }
}