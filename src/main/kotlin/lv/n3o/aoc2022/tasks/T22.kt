package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import lv.n3o.aoc2022.coords.C3
import kotlin.math.min

class T22(input: Input) : Task(input) {
    private val data = input.asBlocks()
    private val map = data[0].asCoordGrid(false).filterValues { it != ' ' }
    private val instructions = data[1].raw().fold(listOf<Instruction>()) { acc, c ->
        when (c) {
            'R' -> acc + listOf(Instruction.Rotate(true))
            'L' -> acc + listOf(Instruction.Rotate(false))
            else -> if (acc.isNotEmpty() && acc.last() is Instruction.Forward) {
                acc.dropLast(1) + listOf(Instruction.Forward((acc.last() as Instruction.Forward).steps * 10 + c.digitToInt()))
            } else {
                acc + listOf(Instruction.Forward(c.digitToInt()))
            }
        }
    }

    private val minY = map.keys.map { it.y }.toSet().min()
    private val minX = map.keys.filter { it.y == minY }.map { it.x }.sorted().first { map[C2(it, minY)] == '.' }

    private val cubeSize = min(map.keys.map { it.x }.toSet().let { (it.max() + 1) - it.min() } / 3,
        map.keys.map { it.y }.toSet().let { (it.max() + 1) - it.min() } / 3)

    /* Specific to input data, will not work with sample */
    private val cubeProjection = setOf(
        CubeFace(C2(1, 0), up = C3(0, 3, 1), right = C3(2, 0, 0), down = C3(1, 1, 0), left = C3(0, 2, 2)),
        CubeFace(C2(2, 0), up = C3(0, 3, 0), right = C3(1, 2, 2), down = C3(1, 1, 1), left = C3(1, 0, 0)),
        CubeFace(C2(1, 1), up = C3(1, 0, 0), right = C3(2, 0, 3), down = C3(1, 2, 0), left = C3(0, 2, 3)),
        CubeFace(C2(0, 2), up = C3(1, 1, 1), right = C3(1, 2, 0), down = C3(0, 3, 0), left = C3(1, 0, 2)),
        CubeFace(C2(1, 2), up = C3(1, 1, 0), right = C3(2, 0, 2), down = C3(0, 3, 1), left = C3(0, 2, 0)),
        CubeFace(C2(0, 3), up = C3(0, 2, 0), right = C3(1, 2, 3), down = C3(2, 0, 0), left = C3(1, 0, 3))
    ).associateBy { it.position }

    private fun calculateResult(missingNextCalc: (PaD) -> PaD): Int {
        var current = PaD(C2(minX, minY), C2.DIRECTION_RIGHT)

        instructions.forEach { instruction ->
            when (instruction) {
                is Instruction.Rotate -> {
                    current = if (instruction.right) current.rotateDirectionRight() else current.rotateDirectionLeft()
                }

                is Instruction.Forward -> {
                    for (i in 1..instruction.steps) {
                        var next = current.step()

                        if (!map.containsKey(next.position)) {
                            next = missingNextCalc(current)
                            val reverse =
                                missingNextCalc(next.copy(direction = next.direction.rotateLeft().rotateLeft()))
                            if (current.position != reverse.position) error("Reversed position failed")
                        }

                        if (map[next.position] == '.') {
                            current = next
                        } else if (map[next.position] == '#') {
                            break
                        } else {
                            error("Unknown map value ${map[next.position]} at $next")
                        }
                    }
                }
            }
        }

        return 1000 * (current.position.y + 1) + 4 * (current.position.x + 1) + when (current.direction) {
            C2.DIRECTION_RIGHT -> 0
            C2.DIRECTION_DOWN -> 1
            C2.DIRECTION_LEFT -> 2
            C2.DIRECTION_UP -> 3
            else -> error("Unknown direction $current.direction")
        }
    }

    override fun a() = calculateResult { (current, direction) ->
        generateSequence(current) { (it - direction).takeIf { c -> map.containsKey(c) } }.last().let {
            PaD(it, direction)
        }
    }

    override fun b(): Int {
        return calculateResult { current ->
            val currentCube = C2(current.position.x / cubeSize, current.position.y / cubeSize)
            val cubeFace = cubeProjection[currentCube] ?: error("No cube face for $currentCube")
            val nextMod = when (current.direction) {
                C2.DIRECTION_UP -> cubeFace.up
                C2.DIRECTION_DOWN -> cubeFace.down
                C2.DIRECTION_LEFT -> cubeFace.left
                C2.DIRECTION_RIGHT -> cubeFace.right
                else -> error("Unknown direction ${current.direction}")
            }

            var next = current.step()

            next = next.updatePosition { pos, _ -> C2(pos.x % cubeSize, pos.y % cubeSize) }
            if (next.position.x < 0) next = next.updatePosition { pos, _ -> pos + C2(cubeSize, 0) }
            if (next.position.y < 0) next = next.updatePosition { pos, _ -> pos + C2(0, cubeSize) }

            repeat(nextMod.z) {
                next = PaD(
                    C2(cubeSize - next.position.y - 1, next.position.x), C2(-next.direction.y, next.direction.x)
                )
            }
            next.updatePosition { pos, _ -> pos + C2(nextMod.x * cubeSize, nextMod.y * cubeSize) }
        }
    }

    sealed class Instruction {
        data class Forward(val steps: Int) : Instruction()
        data class Rotate(val right: Boolean) : Instruction()
    }

    data class PaD(val position: C2, val direction: C2) {
        fun step() = PaD(position + direction, direction)
        fun rotateDirectionRight() = PaD(position, direction.rotateRight())
        fun rotateDirectionLeft() = PaD(position, direction.rotateLeft())

        fun updatePosition(f: (C2, C2) -> C2) = PaD(f(position, direction), direction)
    }

    data class CubeFace(val position: C2, val up: C3, var right: C3, val down: C3, var left: C3)
}