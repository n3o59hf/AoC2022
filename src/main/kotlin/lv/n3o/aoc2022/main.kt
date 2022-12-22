@file:JvmName("Main")

package lv.n3o.aoc2022

import kotlinx.coroutines.runBlocking
import kotlin.math.min
import kotlin.math.roundToLong
import kotlin.system.measureTimeMillis

private const val FIRST_DAY = 1
private const val LAST_DAY = 25
private const val ANSWER_SIZE = 48
private const val TIME_SIZE = 10
private const val TEST_NEW_TASKS = true
private const val REPEAT_RUNS = 1

private fun ci(number: Int, expectedA: String, expectedB: String): TestCase {
    val paddedNumber = "$number".padStart(2, '0')
    val className = "T$paddedNumber"
    val inputFile = "/i$paddedNumber.txt"
    val taskClass = Class.forName("lv.n3o.aoc2022.tasks.$className")
    val constructor: (Input) -> Task =
        { input -> taskClass.getConstructor(Input::class.java).newInstance(input) as Task }

    return TestCase(
        "T:$paddedNumber", ClasspathInput(inputFile), constructor, expectedA, expectedB
    )
}

val testCases: List<TestCase> = listOf(
    ci(1, "66487", "197301"),
    ci(2, "13924", "13448"),
    ci(3, "8088", "2522"),
    ci(4, "413", "806"),
    ci(5, "TLNGFGMFN", "FGLQJCMBD"),
    ci(6, "1542", "3153"),
    ci(7, "1491614", "6400111"),
    ci(8, "1717", "321975"),
    ci(9, "5695", "2434"),
    ci(10, "12740", "RBPARAGF"),
    ci(11, "111210", "15447387620"),
    ci(12, "339", "332"),
    ci(13, "5198", "22344"),
    ci(14, "892", "27155"),
    ci(15, "5607466", "12543202766584"),
    ci(16, "1828", "2292"),
    ci(17, "3106", "1537175792495"),
    ci(18, "3526", "2090"),
    ci(19, "1834", "2240"),
    ci(20, "13183", "6676132372578"),
    ci(21, "268597611536314", "3451534022348"),
    ci(22, "149138", "153203"),
)

fun main() = runBlocking {
    //If tasks not registered are present, execute first such task
    if (TEST_NEW_TASKS) if (testNewTasks()) return@runBlocking
    var minTime = Long.MAX_VALUE
    (1..REPEAT_RUNS).forEach { runId ->
        val time = measureTimeMillis {
            val results = testCases.asSequence().map(::executeTask)

            println()
            printHeader()

            results.forEach { result ->
                if (!result.passA || !result.passB) printTableSeparator()

                print("|")
                print(result.name.padCenter(6, ' '))

                print("| ")
                print(if (result.passA) "OK" else if (result.answerA is TestAnswer.Failure) "E:" else "F ")
                print((result.answerA.displayText.take(ANSWER_SIZE - 2)).padStart(ANSWER_SIZE - 2, ' '))
                print(" | ")
                print(if (result.passB) "OK" else if (result.answerB is TestAnswer.Failure) "E:" else "F ")
                print((result.answerB.displayText.take(ANSWER_SIZE - 2)).padStart(ANSWER_SIZE - 2, ' '))
                print(" | ")
                print(result.time.toString().padStart(TIME_SIZE, ' '))
                println(" |")

                if (!result.passA || !result.passB) {
                    print("|")
                    print(" ".repeat(6))
                    print("| ")
                    if (!result.passA) {
                        print("A:")
                        print((result.tc.expectedA.take(ANSWER_SIZE - 2)).padStart(ANSWER_SIZE - 2, ' '))
                    } else {
                        print(" ".repeat(ANSWER_SIZE))
                    }
                    print(" | ")
                    if (!result.passB) {
                        print("A:")
                        print((result.tc.expectedB.take(ANSWER_SIZE - 2)).padStart(ANSWER_SIZE - 2, ' '))
                    } else {
                        print(" ".repeat(ANSWER_SIZE))
                    }
                    print(" | ")
                    print(" ".repeat(TIME_SIZE))
                    println(" |")
                    printTableSeparator()
                    if (result.answerA is TestAnswer.Failure) {
                        println()
                        result.answerA.exception.printStackTrace(System.out)
                        println()
                    }
                    if (result.answerB is TestAnswer.Failure) {
                        println()
                        result.answerB.exception.printStackTrace(System.out)
                        println()
                    }

                    if (result.answerA is TestAnswer.Failure || result.answerB is TestAnswer.Failure) {
                        printHeader()
                    }

                }
            }
            printTableSeparator()
        }
        minTime = min(minTime, time)
        @Suppress("KotlinConstantConditions")
        if (REPEAT_RUNS != 1) {
            println("RUN ${runId.toString().padStart(5, '0')}\tExecution time: $time")
        } else {
            println("Execution time: $time")
        }
        println()
    }
    @Suppress("KotlinConstantConditions")
    if (REPEAT_RUNS != 1) {
        println("Fastest run: $minTime")
        println()
    }
}

fun executeTask(tc: TestCase): TestResult {
    val timeStart = System.nanoTime()

    val task = tc.executor(tc.input)

    val answerA = try {
        TestAnswer.Success(task.a().toString())
    } catch (e: Exception) {
        TestAnswer.Failure(e)
    }

    val answerB = try {
        TestAnswer.Success(task.b().toString())
    } catch (e: Exception) {
        TestAnswer.Failure(e)
    }

    val timeEnd = System.nanoTime()

    return TestResult(
        tc, answerA, answerB, ((timeEnd - timeStart) / 1_000_000.0).roundToLong()
    )
}

fun testNewTasks(): Boolean {
    (FIRST_DAY..LAST_DAY).forEach { i ->
        try {
            val c = ci(i, "", "")
            if (testCases.none { it.name == c.name }) {
                println("Executing ${c.name}")
                val time = measureTimeMillis {
                    val task = c.executor(c.input)
                    task.debugListener = { s ->
                        println(s)
                    }
                    println(task.a())
                    println(task.b())
                }
                println("Time: $time")
                return true
            }
        } catch (ignored: ClassNotFoundException) {
        }
    }
    return false
}

fun printTableSeparator() {
    print("|")
    print("-".repeat(6))
    print("+")
    print("-".repeat(ANSWER_SIZE + 2))
    print("+")
    print("-".repeat(ANSWER_SIZE + 2))
    print("+")
    print("-".repeat(TIME_SIZE + 2))
    println("|")
}

fun printHeader() {
    printTableSeparator()
    println(
        "| TASK | ${"Answer A".padCenter(ANSWER_SIZE)} | ${"Answer B".padCenter(ANSWER_SIZE)} | ${
            "Time".padCenter(
                TIME_SIZE
            )
        } |"
    )
    printTableSeparator()
}

class TestCase(
    val name: String,
    val input: Input,
    val executor: (Input) -> Task,
    val expectedA: String,
    val expectedB: String,
)

sealed class TestAnswer(val displayText: String) {
    class Success(val result: String) : TestAnswer(result)
    class Failure(val exception: Exception) : TestAnswer(exception.toString())

    override fun toString() = displayText
}

class TestResult(
    val tc: TestCase,
    val answerA: TestAnswer,
    val answerB: TestAnswer,
    val time: Long,
) {
    val name get() = tc.name

    val passA get() = answerA is TestAnswer.Success && tc.expectedA == answerA.result
    val passB get() = answerB is TestAnswer.Success && tc.expectedB == answerB.result
}

fun String.padCenter(targetLength: Int, char: Char = ' ') = if (this.length < targetLength) {
    val difference = targetLength - this.length
    val start = difference / 2
    this.padStart(start + this.length, char).padEnd(targetLength, char)
} else this