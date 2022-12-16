package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import lv.n3o.aoc2022.coords.C3
import lv.n3o.aoc2022.limit
import lv.n3o.aoc2022.normalize
import kotlin.math.abs


class T15(input: Input) : Task(input) {
    private val data = input.asLines().map { line ->
        val (sensorX, sensorY, beaconX, beaconY) = line.split(" ")
            .map { part -> part.filter { it.isDigit() || it == '-' } }.filter { it.isNotBlank() }
        C2(sensorX.toInt(), sensorY.toInt()) to C2(beaconX.toInt(), beaconY.toInt())
    }
    private val sensorsWithRadius = data.map { (s, b) -> C3(s.x, s.y, s.distance(b)) }
    private val beacons = data.map { it.second }.toSet()

    private val testY = 2000000

    override fun a() = sensorsWithRadius
        .map { C2(it.x, it.z - abs(it.y - testY)) }
        .filter { it.y >= 0 }
        .map { (x, y) -> (x - y)..(x + y) }
        .normalize()
        .sumOf { it.count() } - beacons.count { it.y == testY }

    override fun b(): Long {
        for (y in 0..4000000) {
            sensorsWithRadius
                .map { C2(it.x, it.z - abs(it.y - y)) }
                .filter { it.y >= 0 }
                .map { (x, d) -> ((x - d)..(x + d)).limit(0, 4000000) }
                .normalize()
                .let { split ->
                    if (split.size == 2) return ((split.first().last + 1).toLong() * 4000000L) + y.toLong()
                }
        }
        error("Not found")
    }

}

// a: 5607466

//b:
// 9902383 too low
// 1898262264 too low
// 12543202766584 correct