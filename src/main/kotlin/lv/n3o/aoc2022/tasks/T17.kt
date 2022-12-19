package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import lv.n3o.aoc2022.coords.shift
import lv.n3o.aoc2022.infinite

class T17(input: Input) : Task(input) {
    private val data = input.raw().toCharArray().asList()

    private val gustSequence = data.infinite().map { if (it == '<') C2.DIRECTION_LEFT else C2.DIRECTION_RIGHT }
    private val rockSequence = listOf(
        setOf(C2(0, 0), C2(1, 0), C2(2, 0), C2(3, 0)),
        setOf(C2(1, 0), C2(1, 1), C2(1, 2), C2(0, 1), C2(2, 1)),
        setOf(C2(0, 0), C2(1, 0), C2(2, 0), C2(2, 1), C2(2, 2)),
        setOf(C2(0, 0), C2(0, 1), C2(0, 2), C2(0, 3)),
        setOf(C2(0, 0), C2(0, 1), C2(1, 0), C2(1, 1))
    ).infinite()
    private val vertical = C2(0, -1)
    private val cleanupHeight = 100

    override fun a(): Int {
        val field = mutableSetOf<C2>()
        val moves = gustSequence.iterator()
        val rocks = rockSequence.iterator()
        var highestRock = -1

        for (i in 1..2022) {
            var newRock = rocks.next().shift(C2(2, highestRock + 4))

            while (true) {
                val horizontal = moves.next()
                var shiftedRock = newRock.shift(horizontal)
                if (!shiftedRock.any { it.x < 0 || it.x > 6 } && field.intersect(shiftedRock).isEmpty()) {
                    newRock = shiftedRock
                }

                shiftedRock = newRock.shift(vertical)
                if (shiftedRock.any { it.y < 0 } || field.intersect(shiftedRock).isNotEmpty()) {
                    field.addAll(newRock)
                    highestRock = highestRock.coerceAtLeast(newRock.maxOf { it.y })

                    field.removeAll { it.y < highestRock - cleanupHeight }

                    break
                } else {
                    newRock = shiftedRock
                }
            }
        }

        return highestRock + 1

    }

    override fun b(): Long {
        val field = mutableSetOf<C2>()
        val moves = gustSequence.iterator()
        val rocks = rockSequence.iterator()
        var highestRock = -1

        fun makeAMove(): Set<C2> {
            var newRock = rocks.next().shift(C2(2, highestRock + 4))

            while (true) {
                val horizontal = moves.next()
                var shiftedRock = newRock.shift(horizontal)
                if (!shiftedRock.any { it.x < 0 || it.x > 6 } && field.intersect(shiftedRock).isEmpty()) {
                    newRock = shiftedRock
                }

                shiftedRock = newRock.shift(vertical)
                if (shiftedRock.any { it.y < 0 } || field.intersect(shiftedRock).isNotEmpty()) {
                    field.addAll(newRock)
                    highestRock = highestRock.coerceAtLeast(newRock.maxOf { it.y })

                    field.removeAll { it.y < highestRock - cleanupHeight }

                    return newRock
                } else {
                    newRock = shiftedRock
                }
            }
        }

        fun findRepeatedSequenceFromEnd(list: List<Int>): List<Int>? {
            if (list.size < 6) return null
            val reversedList = list.asReversed()
            for (d in (reversedList.size / 2) downTo (5)) {
                val a = reversedList.subList(0, d)
                val b = reversedList.subList(d, 2 * d)
                if (a == b) return a.reversed()
            }
            return null
        }

        val sequence = mutableListOf<Int>()
        while (true) {
            val oldHighest = highestRock
            // one full cycle of shapes, possible point of repetition
            repeat(5) { makeAMove() }
            sequence.add(highestRock - oldHighest)

            val cycle = findRepeatedSequenceFromEnd(sequence)

            if (cycle != null) {
                val movesDone = sequence.size * 5
                val cycleMoves = cycle.size * 5
                val movesTotal = 1000000000000L
                val movesLeft = movesTotal - movesDone
                val cyclesToSkip = movesLeft / cycleMoves
                val movesToAdd = movesLeft % cycleMoves
                return highestRock.toLong() + cycle.sum()
                    .toLong() * cyclesToSkip + cycle.take(movesToAdd.toInt() / 5).sum().toLong() + 1
            }

        }
    }
}


