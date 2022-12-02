package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import kotlin.streams.toList


class T02(input: Input) : Task(input) {
    enum class Move(val move: Char, val score: Int) {
        ROCK('A', 1), PAPER('B', 2), SCISSORS('C', 3);

        companion object {
            val mapping: Map<Char, Move> = Move.values().associateBy { it.move }
        }
    }

    private val data = input.asLines().map { line -> line.chars().toList().map(Int::toChar).let { it[0] to it[2] } }
    override fun a(): Int {
        val points = mapOf(
            (Move.ROCK to Move.SCISSORS) to 0,
            (Move.PAPER to Move.ROCK) to 0,
            (Move.SCISSORS to Move.PAPER) to 0,
            (Move.ROCK to Move.ROCK) to 3,
            (Move.PAPER to Move.PAPER) to 3,
            (Move.SCISSORS to Move.SCISSORS) to 3,
            (Move.ROCK to Move.PAPER) to 6,
            (Move.PAPER to Move.SCISSORS) to 6,
            (Move.SCISSORS to Move.ROCK) to 6,
        ).toMap()

        return data.map { (o, u) ->
            Move.mapping.getValue(o) to Move.mapping.getValue((u.code + 'A'.code - 'X'.code).toChar())
        }.sumOf { (o, u) -> u.score + points[o to u]!! }
    }

    override fun b(): Int {
        val santaMove = listOf(
            Move.SCISSORS, Move.ROCK, Move.PAPER, Move.SCISSORS, Move.ROCK
        )

        return data.sumOf { (o, u) ->
            val move = u.code - 'Y'.code
            val position = Move.mapping.getValue(o).score
            santaMove[move + position].score + (move + 1) * 3
        }
    }
}