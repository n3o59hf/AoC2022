package lv.n3o.aoc2022.tasks

import lv.n3o.aoc2022.Input
import lv.n3o.aoc2022.Task
import lv.n3o.aoc2022.resettableLazy


class T07(input: Input) : Task(input) {
    private val data = input.asLines()

    private val directories: List<Long>

    init {
        val root = Node("/", parent = null, directory = true)
        var wd = root
        for (line in data) {
            if (line.startsWith("$ cd")) {
                    val dir = line.substring(5)
                    wd = when (dir) {
                        "/" -> root
                        ".." -> wd.parent ?: root
                        else -> wd.addChild(dir, true)
                    }
                }
            else if (!line.startsWith("$ ")) {
                val (size, name) = line.split(" ")
                if (size == "dir") {
                    wd.addChild(name, true)
                } else {
                    wd.addChild(name, false, size.toLong())
                }
            }
        }

        fun traverseTree(wd: Node = root): Sequence<Node> = sequence {
            yield((wd))
            for (child in wd.list()) {
                yieldAll(traverseTree(child))
            }
        }

        directories = traverseTree().filter { it.directory }.map { it.totalSize }.toList().sorted()
    }


    override fun a() = directories.takeWhile { it <= 100000L }.sum()

    override fun b() = directories.first { it > directories.last() - 40000000L}

    class Node(
        private val name: String,
        val directory: Boolean,
        val parent: Node?,
        private val size: Long = 0L
    ) {
        private val children: MutableList<Node> = mutableListOf()
        private val totalSizeHandle = resettableLazy { size + children.sumOf { it.totalSize } }
        val totalSize: Long by totalSizeHandle

        fun addChild(name: String, directory: Boolean, size: Long = 0L) =
            children.firstOrNull { it.name == name } ?: Node(
                name,
                directory,
                this,
                size = size
            ).also {
                children.add(it)
                totalSizeHandle.reset()
            }

        fun list() = children.toList()
    }
}