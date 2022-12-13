package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task

class T13(input: Input) : Task(input) {
    private val data = input.asLinesPerBlock().map { (a, b) -> parse(a) to parse(b) }
    private val dividers = listOf("[[2]]", "[[6]]").map { parse(it) }

    override fun a() = data
        .withIndex()
        .filter { (_, pair) -> pair.first < pair.second }
        .sumOf { (i, _) -> i + 1 }

    override fun b() = (data.flatMap { (a, b) -> listOf(a, b) } + dividers)
        .sorted()
        .withIndex()
        .filter { (_, p) -> p in dividers }
        .fold(1) { acc, (i, _) -> acc * (i + 1) }

    private fun parse(s: String): Package {
        if (s.startsWith("[")) {
            val parts = sequence {
                var depth = 1
                var index = 1
                var accumulator = ""
                while (depth != 0) {
                    when (s[index]) {
                        '[' -> {
                            depth++; accumulator += s[index]
                        }
                        ']' -> {
                            depth--; if (depth != 0) accumulator += s[index]
                        }
                        ',' -> {
                            if (depth == 1) {
                                yield(accumulator); accumulator = ""
                            } else {
                                accumulator += s[index]
                            }
                        }
                        else -> accumulator += s[index]
                    }
                    index++
                }
                if (accumulator.isNotBlank()) yield(accumulator)
            }
            return Package.PList(parts.map { parse(it) }.toList())

        } else {
            return Package.PInt(s.toInt())
        }
    }

    private sealed class Package : Comparable<Package> {
        data class PList(val packages: List<Package>) : Package() {
            override fun compareTo(other: Package): Int {
                when (other) {
                    is PList -> {
                        for (i in packages.indices) {
                            if (i >= other.packages.size) return 1
                            val c = packages[i].compareTo(other.packages[i])
                            if (c != 0) return c
                        }
                        return packages.size.compareTo(other.packages.size)
                    }
                    is PInt -> return compareTo(PList(listOf(other)))
                }
            }
        }

        data class PInt(val value: Int) : Package() {
            override fun compareTo(other: Package): Int = when (other) {
                is PInt -> value.compareTo(other.value)
                is PList -> PList(listOf(this)).compareTo(other)
            }
        }
    }
}