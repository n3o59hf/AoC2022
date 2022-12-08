package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import lv.n3o.aoc2022.coords.toIntField

class T08(input: Input) : Task(input) {
    private val data = input.asCoordGrid().mapValues { (_, v) -> v.digitToInt() }.toIntField(-1)

    override fun a() = data
        .borders
        .filter { !data.isCorner(it) }
        .flatMap { c ->
            sequence {
                var height = -1
                val direction = data.borderDirection(c)
                var current = c
                do {
                    if (height < data[current]) {
                        height = data[current]
                        yield(current)
                        if (height == 9) break
                    }
                    current += direction
                } while (current in data)
            }
        }
        .let { it + data.corners }
        .toSet()
        .size


    override fun b() = data
        .map { coord, limit ->
            C2.DIRECTIONS.fold(1) { score, direction ->
                var c = coord
                while ((c + direction) in data && (data[c] < limit || c == coord)) {
                    c += direction
                }
                score * coord.distance(c)
            }
        }
        .max()
}
