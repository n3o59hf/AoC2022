package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task

class T25(input: Input) : Task(input) {
    private val data = input.asLines().map {
        it.reversed().mapIndexed { i, c ->
            val multiplier = if (i == 0) 1L else (1 until i).fold(5L) { a, _ -> a * 5L }
            val digit = when (c) {
                '=' -> -2L
                '-' -> -1L
                '0' -> 0L
                '1' -> 1L
                '2' -> 2L
                else -> error("Unknown char $c")
            }
            multiplier * digit
        }.sum()
    }

    override fun a(): String {
        var sum = data.sum()
        var result = ""
        while (sum > 0) {
            var digit = sum % 5
            if (digit > 2) digit -= 5
            sum -= digit
            sum /= 5
            result += when (digit) {
                -2L -> '='
                -1L -> '-'
                0L -> '0'
                1L -> '1'
                2L -> '2'
                else -> error("Unknown digit $digit")
            }
        }
        return result.reversed()
    }

    override fun b() = ""

}
