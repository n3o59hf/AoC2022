package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.calculateBoundingBox
import lv.n3o.aoc2022.coords.C2


class T24(input: Input) : Task(input) {
    private val data = input.asCoordGrid().filterValues { it != '#' }
    val movementField = data.keys
    val entrance = data.keys.min()
    val exit = data.keys.max()
    val blizzardBox = (data.keys - entrance - exit).calculateBoundingBox()
    val minX = blizzardBox.first.x
    val maxX = blizzardBox.second.x
    val minY = blizzardBox.first.y
    val maxY = blizzardBox.second.y

    val initialBlizards = data
        .mapNotNull { (key, value) ->
            when (value) {
                '>' -> C2.DIRECTION_RIGHT
                '<' -> C2.DIRECTION_LEFT
                '^' -> C2.DIRECTION_UP
                'v' -> C2.DIRECTION_DOWN
                else -> null
            }?.let { d -> Blizzard(key, d) }
        }

    override fun a(): Int {
        var steps = 0

        var field = Field(setOf(entrance), initialBlizards)

        while(!field.positions.contains(exit)) {
            steps++
            field = field.next()
        }
        return steps
    }

    override fun b(): Int {
        var steps = 0
        var field = Field(setOf(entrance), initialBlizards)

        while(!field.positions.contains(exit)) {
            steps++
            field = field.next()
        }

        field = Field(setOf(exit), field.blizzards)
        while(!field.positions.contains(entrance)) {
            steps++
            field = field.next()
        }

        field = Field(setOf(entrance), field.blizzards)
        while(!field.positions.contains(exit)) {
            steps++
            field = field.next()
        }
        return steps

    }

    inner class Field(val positions: Set<C2>, val blizzards:List<Blizzard>) {
        fun next(): Field {
            val nextBlizzards = blizzards.map { it.next() }
            val blizzardBlocked = nextBlizzards.map { it.position }.toSet()
            val nextPositions = positions
                .flatMap { it.neighbors4() + it }
                .filter { it in movementField && it !in blizzardBlocked}
                .toSet()
            return Field(nextPositions, nextBlizzards)
        }
    }

    inner class Blizzard(val position: C2, val direction: C2) {
        fun next() = Blizzard((position + direction).let { (x, y) ->
            C2( when(x) {
                in minX..maxX -> x
                minX-1 -> maxX
                maxX + 1 -> minX
                else -> error("X out of bounds $x")
            }, when(y) {
                in minY..maxY -> y
                minY-1 -> maxY
                maxY + 1 -> minY
                else -> error("Y out of bounds $y")
            })
        }, direction)
    }

}