package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task

class T16(input: Input) : Task(input) {
    private val data = input
        .asLines()
        .map { line ->
            val (valve, tunnels) = line.split(";")
            val valveId = valve.drop(6).take(2)
            val valveFlowRate = valve.split("=").last().toInt()
            val tunnelIDs = tunnels.split(", ").map { it.takeLast(2) }
            Entry(valveId, valveFlowRate, tunnelIDs)
        }
        .associateBy { it.valve }

    private val startValve = data["AA"]!!

    private class StrategyResult(val pressureRelieved: Int, val closedValves: Set<Entry>) {
        operator fun plus(pressure: Int) = StrategyResult(pressureRelieved + pressure, closedValves)
    }

    private fun traceBestStrategy(time: Int, currentLocation: Entry, closedValves: Set<Entry>): StrategyResult {
        if (closedValves.isEmpty()) return StrategyResult(0, closedValves)
        val possibleMoves = closedValves.filter { currentLocation.findDistance(it) + 1 < time }

        val result = possibleMoves.map { next ->
            val timeLeft = time - currentLocation.findDistance(next) - 1
            val newClosedValves = closedValves - next
            traceBestStrategy(timeLeft, next, newClosedValves).let { it + (timeLeft * next.flow) }
        }
        return result.maxByOrNull { it.pressureRelieved } ?: StrategyResult(0, closedValves)
    }

    override fun a(): Int {
        val functioningValves = data.filterValues { it.flow > 0 }.map { it.value }.toSet()
        return traceBestStrategy(30, startValve, functioningValves).pressureRelieved
    }

    override fun b(): Int {
        val functioningValves = data.filterValues { it.flow > 0 }.map { it.value }.toSet()
        val santa = traceBestStrategy(26, startValve, functioningValves)
        val elephant = traceBestStrategy(26, startValve, santa.closedValves)
        return santa.pressureRelieved + elephant.pressureRelieved
    }

    private inner class Entry(val valve: String, val flow: Int, val connections: List<String>) {
        private val distanceTable = mutableMapOf<String, Int>()
        private val connectionRefs by lazy { connections.map { data[it]!! } }

        fun findDistance(to: Entry): Int {
            distanceTable[to.valve]?.let { return it }
            val visited = mutableSetOf(this)
            var steps = 0
            var current = listOf(this)
            while (!current.any { it == to }) {
                visited.addAll(current)
                current = current.flatMap { it.connectionRefs } - visited
                steps++
            }

            distanceTable[to.valve] = steps
            return steps
        }
    }
}