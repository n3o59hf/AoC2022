package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.coords.C2
import lv.n3o.aoc2022.ocr.recognize4X6
import kotlin.math.absoluteValue

class T10(input: Input) : Task(input) {
    private val data = input.asLines().map { OpCode.parse(it) }

    override fun a(): Int {
        val cpu = CPU(data)
        val interestingInstructions = setOf(20, 60, 100, 140, 180, 220).associateWith { 0 }.toMutableMap()
        var instruction = 1
        do {
            if (instruction in interestingInstructions.keys) {
                interestingInstructions[instruction] = cpu.regX
            }
            instruction++
        } while (cpu.tick())

        return interestingInstructions.map { (i, v) -> i * v }.sum()
    }

    override fun b(): String {
        val cpu = CPU(data)
        val screen = Screen()
        do {
            screen.tick(cpu.regX)
        } while (cpu.tick())

        return screen.display.recognize4X6()
    }

    class CPU(private val instructions: List<Instruction>) {
        var regX = 1
            private set
        private var cycleCounter = 0
        private var pc = 0

        fun tick(): Boolean {
            if (instructions.size <= pc) return false

            val op = instructions[pc]

            cycleCounter++

            if (op.op.cycles == cycleCounter) {
                when (op.op) {
                    OpCode.ADDX -> regX += op.args[0]
                    OpCode.NOOP -> {
                        /*NOOP*/
                    }
                }
                cycleCounter = 0
                pc++
            }
            return true
        }
    }

    class Screen {
        private val pixels = mutableSetOf<C2>()
        val display get()= pixels.toSet()
        private var cycle = 0

        fun tick(spriteX: Int) {
            val sx = cycle % 40
            val sy = (cycle / 40)
            if ((sx - spriteX).absoluteValue < 2) {
                pixels += C2(sx, sy)
            }
            cycle++
        }

    }

    enum class OpCode(val code: String, val cycles: Int) {
        ADDX("addx", 2),
        NOOP("noop", 1);

        companion object {
            val map = values().associateBy { it.code }
            fun parse(line: String) = when {
                line.startsWith("addx") -> Instruction(ADDX, line.substring(5).toInt())
                line == "noop" -> Instruction(NOOP)
                else -> error("Unknown instruction $line")
            }
        }
    }

    class Instruction(val op: OpCode, vararg val args: Int)
}