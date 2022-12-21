package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import kotlin.math.absoluteValue
import kotlin.math.sign


class T21(input: Input) : Task(input) {
    private val data = input.asLines().map { line ->
        val (name, operation) = line.split(": ")
        val opArgs = operation.split(" ")

        if (opArgs.size == 1) {
            NumberScream(name, opArgs[0].toLong())
        } else {
            OperationScream(name, opArgs[0], Op.fromString(opArgs[1]), opArgs[2])
        }
    }.associateBy { it.name }

    override fun a() = data["root"]?.resolve()

    override fun b(): Long {
        val aExp = (data["root"] as? OperationScream)?.aRef?.reduceWithHuman()
        val bExp = (data["root"] as? OperationScream)?.bRef?.reduceWithHuman()

        val result = aExp?.takeIf { it is NumberScream }?.resolve() ?: bExp?.resolve() ?: error("No result?")
        val expression = aExp?.takeIf { it is ResolvedScream } ?: bExp ?: error("No Expression?")
        val humanScream = expression.getNamed("humn") as? HumanScream ?: error("No human scream")

        humanScream.number = 0L
        val withZero = expression.resolve()
        humanScream.number = 1L
        val withOne = expression.resolve()

        var humanNumber = 0L
        val magnitude = (withOne - withZero).absoluteValue * 2
        val direction = (withZero-withOne).sign.toLong()
        while(true) {
            humanScream.number = humanNumber
            val resolve = expression.resolve()
            if(resolve == result) {
                return humanNumber
            } else {
                val correction =  (resolve - result)/magnitude
                humanNumber += correction.coerceAtLeast(1L) * direction
            }
        }
    }

    abstract inner class MonkeyScream(val name: String) {
        abstract fun resolve(): Long
        abstract fun expression(): String
        abstract fun reduceWithHuman(): MonkeyScream

        abstract fun getNamed(name: String): MonkeyScream?
    }

    inner class NumberScream(name: String, val number: Long) : MonkeyScream(name) {
        override fun resolve() = number
        override fun expression() = "$number"
        override fun reduceWithHuman() = if (name == "humn") HumanScream() else this
        override fun getNamed(name: String) = if (this.name == name) this else null
    }

    inner class OperationScream(name: String, val a: String, val op: Op, val b: String) : MonkeyScream(name) {
        val aRef: MonkeyScream by lazy { data[a] ?: error("No monkey named $a") }
        val bRef: MonkeyScream by lazy { data[b] ?: error("No monkey named $b") }
        private val number get() = op.apply(aRef.resolve(), bRef.resolve())

        override fun resolve() = number
        override fun expression() = "(${aRef.expression()} ${op.symbol} ${bRef.expression()})"
        override fun reduceWithHuman(): MonkeyScream {
            val aRed = aRef.reduceWithHuman()
            val bRed = bRef.reduceWithHuman()
            return if (aRed is HumanScream || bRed is HumanScream || aRed is ResolvedScream || bRed is ResolvedScream) {
                ResolvedScream(name, aRed, op, bRed)
            } else {
                NumberScream(name, op.apply(aRed.resolve(), bRed.resolve()))
            }
        }

        override fun getNamed(name: String) = aRef.getNamed(name) ?: bRef.getNamed(name)
    }

    inner class HumanScream : MonkeyScream("humn") {
        var number: Long = 0
        override fun resolve() = number
        override fun expression() = "x"
        override fun reduceWithHuman() = this
        override fun getNamed(name: String) = if(name == "humn") this else null
    }

    inner class ResolvedScream(name: String, val a: MonkeyScream, val op: Op, val b: MonkeyScream) :
        MonkeyScream(name) {
        override fun resolve() = op.apply(a.resolve(), b.resolve())
        override fun expression() = "(${a.expression()}${op.symbol}${b.expression()})"
        override fun reduceWithHuman() = this
        override fun getNamed(name: String) = a.getNamed(name) ?: b.getNamed(name)
    }

    enum class Op(val symbol: String) {
        PLUS("+"), MINUS("-"), TIMES("*"), DIVIDE("/");

        fun apply(a: Long, b: Long) = when (this) {
            PLUS -> a + b
            MINUS -> a - b
            TIMES -> a * b
            DIVIDE -> a / b
        }

        companion object {
            private val all = values().associateBy { it.symbol }
            fun fromString(s: String) = all[s] ?: error("No op $s")
        }
    }
}