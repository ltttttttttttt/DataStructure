package com.lt.data_structure.mutex

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * creator: lt  2026/2/21  lt.dygzs@qq.com
 * effect : 线程安全的MutableList(使用协程)
 * warning:
 */
class CoroutineList<T>(
    val innerList: MutableList<T>,
    val mutex: Mutex = Mutex(),
) {
    suspend fun getSize(): Int = mutex.withLock {
        innerList.size
    }

    suspend fun add(element: T): Boolean = mutex.withLock {
        innerList.add(element)
    }

    suspend fun remove(element: T): Boolean = mutex.withLock {
        innerList.remove(element)
    }

    suspend fun addAll(elements: Collection<T>): Boolean = mutex.withLock {
        innerList.addAll(elements)
    }

    suspend fun addAll(index: Int, elements: Collection<T>): Boolean = mutex.withLock {
        innerList.addAll(index, elements)
    }

    suspend fun removeAll(elements: Collection<T>): Boolean = mutex.withLock {
        innerList.removeAll(elements)
    }

    suspend fun retainAll(elements: Collection<T>): Boolean = mutex.withLock {
        innerList.retainAll(elements)
    }

    suspend fun clear() = mutex.withLock {
        innerList.clear()
    }

    suspend operator fun set(index: Int, element: T): T = mutex.withLock {
        innerList.set(index, element)
    }

    suspend fun add(index: Int, element: T) = mutex.withLock {
        innerList.add(index, element)
    }

    suspend fun removeAt(index: Int): T = mutex.withLock {
        innerList.removeAt(index)
    }

    suspend fun listIterator(): ListIterator<T> = mutex.withLock {
        innerList.toList().listIterator()
    }

    suspend fun listIterator(index: Int): ListIterator<T> = mutex.withLock {
        innerList.toList().listIterator(index)
    }

    suspend fun subList(fromIndex: Int, toIndex: Int): List<T> = mutex.withLock {
        innerList.toList().subList(fromIndex, toIndex)
    }

    suspend fun isEmpty(): Boolean = mutex.withLock {
        innerList.isEmpty()
    }

    suspend operator fun contains(element: T): Boolean = mutex.withLock {
        innerList.contains(element)
    }

    suspend fun containsAll(elements: Collection<T>): Boolean = mutex.withLock {
        innerList.containsAll(elements)
    }

    suspend operator fun get(index: Int): T = mutex.withLock {
        innerList[index]
    }

    suspend fun indexOf(element: T): Int = mutex.withLock {
        innerList.indexOf(element)
    }

    suspend fun lastIndexOf(element: T): Int = mutex.withLock {
        innerList.lastIndexOf(element)
    }

    suspend operator fun iterator(): Iterator<T> = mutex.withLock {
        innerList.toList().iterator()
    }

    suspend fun toList(): List<T> = mutex.withLock {
        innerList.toList()
    }
}

fun <T> MutableList<T>.toCoroutineList(): CoroutineList<T> = CoroutineList(this)