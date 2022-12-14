@file:Suppress("unused")

package lv.n3o.aoc2022

import lv.n3o.aoc2022.coords.C2
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KProperty

private val startTime = System.nanoTime()

val timeFromApplicationStart get() = System.nanoTime() - startTime

fun Long.formatTime(): String =
    (this / 1000000.0).roundToInt().toString().padStart(6, ' ').chunked(3).joinToString(" ") + "ms"

val String.cleanLines get() = lines().map { it.trim() }.filter { it.isNotBlank() }

fun <T> List<T>.infinite() = sequence {
    while (true) {
        yieldAll(this@infinite)
    }
}

fun <T> List<T>.permute(): List<List<T>> {
    if (size == 1) return listOf(this)

    val permutations = mutableListOf<List<T>>()
    val movableElement = first()
    for (p in drop(1).permute()) for (i in 0..p.size) {
        val mutation = p.toMutableList()
        mutation.add(i, movableElement)
        permutations.add(mutation)
    }
    return permutations
}

fun <T> List<T>.combinations(): Sequence<Set<T>> = sequence {
    var indexes = intArrayOf()
    while (indexes.size < this@combinations.size) {
        indexes = IntArray(indexes.size + 1) { it }
        while (indexes[0] <= this@combinations.size - indexes.size) {
            yield(indexes.map { this@combinations[it] }.toSet())
            var incrementIndex = indexes.size - 1
            while (incrementIndex >= 0) {
                indexes[incrementIndex] += 1
                if (indexes[incrementIndex] > this@combinations.size - (indexes.size - incrementIndex)) {
                    incrementIndex--
                } else {
                    break
                }
            }
            incrementIndex++
            while (incrementIndex < indexes.size) {
                if (incrementIndex != 0) indexes[incrementIndex] = indexes[incrementIndex - 1] + 1
                incrementIndex++
            }
        }
    }
}

fun gcd(a: Int, b: Int): Int {
    var gcd = a.coerceAtMost(b)
    while (gcd > 0) {
        if (a % gcd == 0 && b % gcd == 0) return gcd
        gcd--
    }
    return -1
}

fun gcd(a: Long, b: Long): Long {
    var gcd = a.coerceAtMost(b)
    while (gcd > 0L) {
        if (a % gcd == 0L && b % gcd == 0L) return gcd
        gcd--
    }
    return -1
}

fun lcm(a: Long, b: Long): Long = abs(a * b) / gcd(a, b)

fun Long.divRoundedUp(divider: Long) = this.toNearestMultipleUp(divider) / divider

fun Long.toNearestMultipleUp(factor: Long): Long {
    val reminder = if (this % factor > 0) 1 else 0
    return ((this / factor) + reminder) * factor
}

fun Int.nextPowerOf2(): Int {
    var number = this
    number--
    number = number or (number shr 1)
    number = number or (number shr 2)
    number = number or (number shr 4)
    number = number or (number shr 8)
    number = number or (number shr 16)
    number++
    return number
}

fun Set<C2>.calculateBoundingBox(): Pair<C2,C2> {
    val minX = minOf { it.x }
    val maxX = maxOf { it.x }
    val minY = minOf { it.y }
    val maxY = maxOf { it.y }
    return C2(minX, minY) to C2(maxX, maxY)
}

fun <T> Map<C2, T>.compactDebugDraw(conversion: (T?) -> Char = { it?.toString()?.get(0) ?: ' ' }) {
    val allKeys = keys

    val maxX = allKeys.map(C2::x).maxOrNull() ?: 1
    val maxY = allKeys.map(C2::y).maxOrNull() ?: 1
    val minX = allKeys.map(C2::x).minOrNull() ?: 1
    val minY = allKeys.map(C2::y).minOrNull() ?: 1

    val output = (minY..maxY).joinToString("\n") { y ->
        (minX..maxX).map { x ->
            conversion(this[C2(x, y)])
        }.joinToString("")
    }
    println("\n$output\n")
}

fun <T> Map<C2, T>.debugDraw(cellWidth: Int = 1, conversion: (T?) -> Any = { it.toString() }) {
    val allKeys = keys

    val maxX = allKeys.map(C2::x).maxOrNull() ?: 1
    val maxY = allKeys.map(C2::y).maxOrNull() ?: 1
    val minX = allKeys.map(C2::x).minOrNull() ?: 1
    val minY = allKeys.map(C2::y).minOrNull() ?: 1


    val cellBorder = (0 until cellWidth).joinToString("") { "-" }
    val verticalSeperator = "\n" + (minX..maxX).joinToString("+", "+", "+") { cellBorder } + "\n"

    val output = "\n$verticalSeperator" + (minY..maxY).map { y ->
        (minX..maxX).map { x ->
            var cell = conversion(this[C2(x, y)]).toString()
            cell = cell.substring(0, cell.length.coerceAtMost(cellWidth))
            if (cell.length < cellWidth) cell = cell.padEnd(cell.length + ((cellWidth - cell.length) / 2))
            if (cell.length < cellWidth) cell = cell.padStart(cellWidth)
            cell
        }.joinToString("|", "|", "|")

    }.joinToString(verticalSeperator) + verticalSeperator
    println("\n$output\n")
}

fun Set<C2>.drawDebug() = associate { it to '#' }.compactDebugDraw()

class MapWithLazy<K, V>(val backingMap: MutableMap<K, V>, val lazy: (K) -> V) : MutableMap<K, V> by backingMap {
    override operator fun get(key: K): V {
        val value = backingMap[key]
        return if (value == null) {
            val lazyValue = lazy(key)
            backingMap[key] = lazyValue
            lazyValue
        } else {
            value
        }
    }
}

fun <K, V> MutableMap<K, V>.withLazy(lazy: (K) -> V) = MapWithLazy(this, lazy)

fun <V> Map<C2, V>.infinite(horizontal: Boolean = false, vertical: Boolean = false) =
    InfiniteMap(this, horizontal, vertical)

class InfiniteMap<V>(val original: Map<C2, V>, val horizontal: Boolean, val vertical: Boolean) {
    val realMinX = original.keys.map { it.x }.minOrNull() ?: error("No data")
    val realMinY = original.keys.map { it.y }.minOrNull() ?: error("No data")
    val realMaxX = original.keys.map { it.x }.maxOrNull() ?: error("No data")
    val realMaxY = original.keys.map { it.y }.maxOrNull() ?: error("No data")
    val xRange = realMinX..realMaxX
    val yRange = realMinY..realMaxY

    operator fun get(c: C2): V {
        val x = when {
            c.x in xRange -> c.x
            c.x < realMinX && horizontal -> {
                var x = c.x
                val diff = xRange.count()
                while (x !in xRange) x += diff
                x
            }

            c.x > realMaxX && horizontal -> {
                var x = c.x
                val diff = xRange.count()
                while (x !in xRange) x -= diff
                x
            }

            else -> error("X out of range")
        }
        val y = when {
            c.y in yRange -> c.y
            c.y < realMinY && horizontal -> {
                var y = c.y
                val diff = yRange.count()
                while (y !in yRange) y += diff
                y
            }

            c.y > realMaxY && horizontal -> {
                var y = c.y
                val diff = yRange.count()
                while (y !in yRange) y -= diff
                y
            }

            else -> error("Y out of range")
        }

        return original[C2(x, y)] ?: error("Non-square coordinate grid")
    }

    fun contains(coord: C2) = (horizontal || coord.x in xRange) && (vertical || coord.y in yRange)
}

fun <E> List<List<E>>.transpose(defaultValue: E) =
    List(maxOf { it.size }) { i -> this.map { it.getOrElse(i) { defaultValue } } }


class MapWithDefault<K, V>(private val map: Map<K, V>, private val default: V) : Map<K, V> by map {
    override fun get(key: K): V = map[key] ?: default
}

class MutableMapWithDefault<K, V>(private val map: MutableMap<K, V>, private val default: V) : MutableMap<K, V> by map {
    override fun get(key: K): V = map[key] ?: default
}

fun <K, V> Map<K, V>.setDefault(value: V) = MapWithDefault(this, value)

fun <K, V> MutableMap<K, V>.setDefault(value: V) = MutableMapWithDefault(this, value)

fun <T> prioritizingComparator(priority: List<T>, fallbackComparator: Comparator<T>) = Comparator { a: T, b: T ->
    val aIndex = priority.indexOf(a)
    val bIndex = priority.indexOf(b)
    when {
        aIndex < 0 && bIndex < 0 -> fallbackComparator.compare(a, b)
        aIndex < 0 -> 1
        bIndex < 0 -> -1
        else -> aIndex - bIndex
    }
}

operator fun <T> Sequence<T>.get(index: Int) = drop(index).first()

val Boolean.sign get() = if (this) 1 else -1
val Boolean.signLong get() = if (this) 1L else -1L

fun <T> Iterable<Collection<T>>.intersectAll() = map { it.toSet() }.reduce { a, b -> a.intersect(b) }

fun <T> resettableLazy(initializer: () -> T) = ResettableLazy(initializer)

class ResettableLazy<T>(private val initializer: () -> T) {
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            value = initializer()
        }
        return value!!
    }

    fun reset() {
        value = null
    }
}

fun Collection<IntRange>.normalize(): Collection<IntRange> {
    if (size < 2) return this
    val result = mutableListOf<IntRange>()
    sortedBy { it.first }.forEach { next ->
        if (result.isEmpty()) {
            result.add(next)
        } else {
            val last = result.last()
            if (last.last >= next.first - 1) {
                if (next.last > last.last) {
                    result[result.lastIndex] = last.first..next.last
                } // else next dropped as it falls into range
            } else {
                result.add(next)
            }
        }
    }
    return result
}


fun IntRange.limit(min: Int, max: Int) = first.coerceIn(min..max)..last.coerceIn(min..max)