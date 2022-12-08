package lv.n3o.aoc2022.coords

class C2IntField(val width: Int, val height: Int, defaultValue: Int = 0) {
    private val max = C2(width - 1, height - 1)
    private val array = IntArray(width * height) { defaultValue }
    var outOfBoundsValue = defaultValue

    val allCoords by lazy { (0 until width).flatMap { x -> (0 until height).map { y -> C2(x, y) } } }
    val borders by lazy {
        ((0 until width).flatMap { x -> listOf(C2(x, 0), C2(x, height - 1)) } +
                (0 until height).flatMap { y -> listOf(C2(0, y), C2(width - 1, y)) }
                ).toSet()
    }
    val corners by lazy { listOf(C2(0, 0), C2(width - 1, 0), C2(0, height - 1), C2(width - 1, height - 1)) }

    constructor(max: C2, defaultValue: Int = 0) : this(max.x + 1, max.y + 1, defaultValue)

    operator fun get(c: C2) = c.arrayIndex(max)?.let { array[it] } ?: outOfBoundsValue
    operator fun set(c: C2, value: Int) = c.arrayIndex(max)?.let { array[it] = value }
    operator fun contains(c: C2) = c.x in 0 until width && c.y in 0 until height

    fun forEach(f: (C2, Int) -> Unit) = allCoords.forEach { f(it, this[it]) }
    fun <T> map(f: (C2, Int) -> T) = allCoords.map { f(it, this[it]) }

    fun isCorner(c: C2) = c in corners
    fun borderDirection(c: C2) = when {
        c.x == 0 -> C2(1, 0)
        c.x == width - 1 -> C2(-1, 0)
        c.y == 0 -> C2(0, 1)
        c.y == height - 1 -> C2(0, -1)
        else -> if(c in corners) error("Corner") else error("Not a border")
    }
}


fun Map<C2, Int>.toIntField(defaultValue: Int) =
    C2IntField(C2(keys.maxOf { it.x }, keys.maxOf { it.y }), defaultValue).also {
        this.forEach { (c, i) -> it[c] = i }
    }
