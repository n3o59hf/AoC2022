package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.get
import lv.n3o.aoc2022.infinite
import kotlin.math.sign

class T20(input: Input) : Task(input) {
    private val data = input.asLines().map { it.toLong() }

    private fun mix(ring: MutableList<IndexedValue<Long>>, order: List<IndexedValue<Long>>) {
        val sequence = order.iterator()

        while (sequence.hasNext()) {
            val key = sequence.next()
            var index = ring.indexOf(key)
            var next = key.value
            if (next == 0L) continue
            next %= data.size - 1

            while (next != 0L) {
                val direction = next.sign
                var nextSwap = index + direction
                if (nextSwap < 0) nextSwap = ring.size - 1
                if (nextSwap >= ring.size) nextSwap = 0

                val a = ring[index]
                val b = ring[nextSwap]
                ring[index] = b
                ring[nextSwap] = a
                index = nextSwap
                next -= direction
            }
        }
    }

    private fun extractEncryptionKey(ring: List<IndexedValue<Long>>): Long {
        val seq = ring.map { it.value }.infinite().dropWhile { it != 0L }

        return seq.take(3001).let {
            it[1000]+it[2000]+it[3000] }
    }

    override fun a(): Long {
        val ring = data.withIndex().toMutableList()
        val order = ring.toList()
        mix(ring, order)
        return extractEncryptionKey(ring)
    }

    override fun b(): Long {
        val ring = data.map { it * 811589153L }.withIndex().toMutableList()
        val order = ring.toList()
        repeat(10) {
            mix(ring, order)
        }
        return extractEncryptionKey(ring)
    }
}