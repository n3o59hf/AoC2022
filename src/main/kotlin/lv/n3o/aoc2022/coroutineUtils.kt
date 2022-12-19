package lv.n3o.aoc2022

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun <T, R> Iterable<T>.parallelMap(transform: suspend (T) -> R): List<R> = runBlocking {
    map { async(Dispatchers.Default) { transform(it) } }.awaitAll()
}