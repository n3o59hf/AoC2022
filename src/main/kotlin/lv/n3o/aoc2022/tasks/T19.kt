package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.parallelMap

class T19(input: Input) : Task(input) {
    private val data = input
        .asLines()
        .map { line ->
            line.filter { it.isDigit() || it == ' ' }.split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        }
        .map { Blueprint(it[0], it[1], it[2], it[3], it[4], it[5], it[6]) }

    private fun doCalculation(blueprint: Blueprint, depth: Int): Int {
        var maxResult = 0

        fun goDeeper(blueprint: Blueprint, state: State) {
            if (state.minutesLeft == 0) {
                maxResult = maxResult.coerceAtLeast(state.geode)
                return
            }

            if (state.maxPossibleGeode < maxResult) return

            val actions = Action.all
                .mapNotNull { action ->
                    val moves = state.howLongToWaitFor(blueprint, action)
                    if (moves == null) null else action to (moves + 1)
                }.takeLast(3) //Do not build more ore bots if we have enough

            actions.forEach { (action, moves) ->
                if (state.minutesLeft - moves < 0) {
                    maxResult = maxResult.coerceAtLeast(state.minuteUpdate(state.minutesLeft).geode)
                } else {
                    state.minuteUpdate(moves - 1).actionUpdate(blueprint, action)
                    val nextStat = state.minuteUpdate(moves).actionUpdate(blueprint, action)
                    goDeeper(blueprint, nextStat)
                }
            }
        }
        goDeeper(blueprint, State(depth))

        return maxResult
    }

    override fun a() = data.parallelMap { bp -> bp.id * doCalculation(bp, 24) }.sum()

    override fun b() = data.take(3).parallelMap { bp -> doCalculation(bp, 32) }.reduce { a, b -> a * b }

    data class Blueprint(
        val id: Int,
        val oreRobotOreCost: Int,
        val clayRobotOreCost: Int,
        val obsidianRobotOreCost: Int, val obsidianRobotClayCost: Int,
        val geodeRobotOreCost: Int, val geodeRobotObsidianCost: Int,
    )

    data class State(
        val minutesLeft: Int,
        val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0,
        val oreRobots: Int = 1, val clayRobots: Int = 0, val obsidianRobots: Int = 0, val geodeRobots: Int = 0
    ) {
        val maxPossibleGeode = geode + minutesLeft * geodeRobots + sumLookupTable[minutesLeft]

        fun minuteUpdate(minutes: Int = 1) = copy(
            minutesLeft = minutesLeft - minutes,
            ore = ore + oreRobots * minutes,
            clay = clay + clayRobots * minutes,
            obsidian = obsidian + obsidianRobots * minutes,
            geode = geode + geodeRobots * minutes
        )

        fun actionUpdate(blueprint: Blueprint, action: Action): State {
            return when (action) {
                Action.ORE_ROBOT -> copy(ore = ore - blueprint.oreRobotOreCost, oreRobots = oreRobots + 1)
                Action.CLAY_ROBOT -> copy(
                    ore = ore - blueprint.clayRobotOreCost,
                    clayRobots = clayRobots + 1
                )
                Action.OBSIDIAN_ROBOT -> copy(
                    ore = ore - blueprint.obsidianRobotOreCost,
                    clay = clay - blueprint.obsidianRobotClayCost,
                    obsidianRobots = obsidianRobots + 1
                )
                Action.GEODE_ROBOT -> copy(
                    ore = ore - blueprint.geodeRobotOreCost,
                    obsidian = obsidian - blueprint.geodeRobotObsidianCost,
                    geodeRobots = geodeRobots + 1
                )
            }
        }

        fun howLongToWaitFor(blueprint: Blueprint, action: Action) = when (action) {
            Action.ORE_ROBOT -> if (ore > blueprint.oreRobotOreCost) 0 else {
                val oreNeeded = (blueprint.oreRobotOreCost - ore).coerceAtLeast(0)
                oreNeeded / oreRobots + (oreNeeded % oreRobots).coerceAtMost(1)
            }

            Action.CLAY_ROBOT -> if (ore > blueprint.clayRobotOreCost) 0 else {
                val oreNeeded = (blueprint.clayRobotOreCost - ore).coerceAtLeast(0)
                oreNeeded / oreRobots + (oreNeeded % oreRobots).coerceAtMost(1)
            }

            Action.OBSIDIAN_ROBOT -> if (clayRobots == 0) null else if (ore >= blueprint.obsidianRobotOreCost && clay >= blueprint.obsidianRobotClayCost) 0 else {
                val oreNeeded = (blueprint.obsidianRobotOreCost - ore).coerceAtLeast(0)
                val clayNeeded = (blueprint.obsidianRobotClayCost - clay).coerceAtLeast(0)
                maxOf(
                    oreNeeded / oreRobots + (oreNeeded % oreRobots).coerceAtMost(1),
                    clayNeeded / clayRobots + (clayNeeded % clayRobots).coerceAtMost(1)
                )
            }

            Action.GEODE_ROBOT -> if (obsidianRobots == 0) null else if (ore >= blueprint.geodeRobotOreCost && obsidian >= blueprint.geodeRobotObsidianCost) 0 else {
                val oreNeeded = (blueprint.geodeRobotOreCost - ore).coerceAtLeast(0)
                val obsidianNeeded = (blueprint.geodeRobotObsidianCost - obsidian).coerceAtLeast(0)
                maxOf(
                    oreNeeded / oreRobots + (oreNeeded % oreRobots).coerceAtMost(1),
                    obsidianNeeded / obsidianRobots + (obsidianNeeded % obsidianRobots).coerceAtMost(1)
                )
            }
        }

        companion object {
            private val sumLookupTable = IntArray(33) { (0..it).sum() }
        }
    }

    enum class Action {
        ORE_ROBOT, CLAY_ROBOT, OBSIDIAN_ROBOT, GEODE_ROBOT;

        companion object {
            val all = values().toSet()
        }
    }
}