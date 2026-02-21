package com.lt.data_structure.mutex

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * creator: lt  2026/2/21  lt.dygzs@qq.com
 * effect : 线程安全的MutableMap(使用协程)
 * warning:
 */
class CoroutineMap<K, V>(
    val innerMap: MutableMap<K, V>,
    val mutex: Mutex = Mutex(),
) {
    suspend fun getSize(): Int = mutex.withLock {
        innerMap.size
    }

    suspend fun getKeys(): Set<K> = mutex.withLock {
        innerMap.toMap().keys
    }

    suspend fun getValues(): Collection<V> = mutex.withLock {
        innerMap.toMap().values
    }

    suspend fun getEntries(): Set<Map.Entry<K, V>> = mutex.withLock {
        innerMap.toMap().entries
    }

    suspend fun put(key: K, value: V): V? = mutex.withLock {
        innerMap.put(key, value)
    }

    suspend operator fun set(key: K, value: V) = mutex.withLock {
        innerMap[key] = value
    }

    suspend fun remove(key: K): V? = mutex.withLock {
        innerMap.remove(key)
    }

    suspend fun putAll(from: Map<out K, V>) = mutex.withLock {
        innerMap.putAll(from)
    }

    suspend fun clear() = mutex.withLock {
        innerMap.clear()
    }

    suspend fun isEmpty(): Boolean = mutex.withLock {
        innerMap.isEmpty()
    }

    suspend fun containsKey(key: K): Boolean = mutex.withLock {
        innerMap.containsKey(key)
    }

    suspend fun containsValue(value: V): Boolean = mutex.withLock {
        innerMap.containsValue(value)
    }

    suspend operator fun get(key: K): V? = mutex.withLock {
        innerMap[key]
    }

    suspend fun toMap(): Map<K, V> = mutex.withLock {
        innerMap.toMap()
    }
}

fun <K, V> MutableMap<K, V>.toCoroutineMap(): CoroutineMap<K, V> = CoroutineMap(this)