package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.calculateBoundingBox
import lv.n3o.aoc2022.coords.C2


class T23(input: Input) : Task(input) {
    private val data = input.asCoordGrid().filterValues { it == '#' }
    private val initialDirections = listOf(C2.DIRECTION_UP, C2.DIRECTION_DOWN, C2.DIRECTION_LEFT, C2.DIRECTION_RIGHT)
    private val adjacentMap = mapOf(
        C2.DIRECTION_UP to listOf(C2.DIRECTION_LEFT, C2.DIRECTION_RIGHT, C2.ZERO).map { C2.DIRECTION_UP + it },
        C2.DIRECTION_DOWN to listOf(C2.DIRECTION_LEFT, C2.DIRECTION_RIGHT, C2.ZERO).map { C2.DIRECTION_DOWN + it },
        C2.DIRECTION_LEFT to listOf(C2.DIRECTION_UP, C2.DIRECTION_DOWN, C2.ZERO).map { C2.DIRECTION_LEFT + it },
        C2.DIRECTION_RIGHT to listOf(C2.DIRECTION_UP, C2.DIRECTION_DOWN, C2.ZERO).map { C2.DIRECTION_RIGHT + it },
    )

    private fun transform(elves: Set<C2>, round: Int): Set<C2> {
        class Elf(var plannedPosition: C2? = null)

        var map = elves.associateWith { Elf() }
        val directions =
            initialDirections.drop(round % initialDirections.size) + initialDirections.take(round % initialDirections.size)

        // Planning
        map.forEach { (coord, elf) ->
            elf.plannedPosition =
                if (coord.neighbors8().any { map.containsKey(it) }) {
                    val delta = directions.firstOrNull { dir ->
                        (adjacentMap[dir] ?: error("No adjacents for $dir")).none { map.containsKey(it + coord) }
                    } ?: C2(0, 0)
                    coord + delta
                } else {
                    coord
                }
        }

        // Checking movement
        do {
            map.forEach { (coord, elf) ->
                if (elf.plannedPosition == null) elf.plannedPosition = coord
            }
            val overcrowded =
                map.values.mapNotNull { it.plannedPosition }.groupBy { it }.mapValues { (_, v) -> v.size }
                    .filterValues { it > 1 }.keys
            map.values.forEach { elf ->
                if (elf.plannedPosition in overcrowded) elf.plannedPosition = null
            }
        } while (map.values.any { it.plannedPosition == null })

        // Moving
        map = map.values.associateBy { elf -> elf.plannedPosition ?: error("Elf without a plan") }

        return map.keys
    }

    override fun a(): Int {
        var map = data.keys

        for (i in 0 until 10) {
            map = transform(map, i)
        }

        val (min, max) = map.calculateBoundingBox()
        val size = (max - min + C2(1, 1)).let { it.x * it.y }

        return size - map.size
    }

    override fun b(): Int {
        var map = data.keys
        var iteration = 0
        while (true) {
            val newMap = transform(map, iteration)
            iteration++
            if (newMap == map) return iteration
            map = newMap
        }
    }
}
