package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import kotlin.math.max
import kotlin.math.min


class T14(input: Input) : Task(input) {
    private val data = input.asLines().flatMap { line ->
        line.split(" -> ").windowed(2, 1).flatMap { (from, to) ->
            val (fromX, fromY) = from.split(",").map { it.toInt() }
            val (toX, toY) = to.split(",").map { it.toInt() }
            if (fromX == toX) {
                (min(fromY, toY)..max(fromY, toY)).map { C2(fromX, it) }
            } else {
                (min(fromX, toX)..max(fromX, toX)).map { C2(it, fromY) }
            }
        }
    }.toSet()

    val moves = listOf(C2(0, 1), C2(-1, 1), C2(1, 1))

    override fun a(): Int {
        val field = data.toMutableSet()
        val yLimit = field.maxOf { it.y } + 1

        outer@ while (true) {
            var current = C2(500, 0)
            inner@ while (true) {
                val next = moves.map { it + current }.firstOrNull { it !in field }
                if (next == null) {
                    field.add(current); break@inner
                }
                if (next.y == yLimit) {
                    break@outer
                }
                current = next
            }
        }
        return (field - data).size
    }

    override fun b(): Int {
        val field = data.toMutableSet()
        val yLimit = field.maxOf { it.y } + 2

        while (C2(500, 0) !in field) {
            var current = C2(500, 0)
            while (true) {
                val next = moves.map { it + current }.firstOrNull { it !in field }
                if (next == null || next.y == yLimit) {
                    field.add(current); break
                }
                current = next
            }
        }
        return (field - data).size
    }
}