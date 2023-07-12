package com.lt.data_structure.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * creator: lt  2023/7/12  lt.dygzs@qq.com
 * effect : 会根据倒计时自动移除数据的list
 * warning: 内部使用ArrayList+Mutex+suspend
 *          线程安全
 * [downTime]倒计时多少毫秒自动移除数据
 */
class DownTimeSuspendList<T>(
    private val downTime: Long,
    private val coroutineScope: CoroutineScope,
) {
    private val list = ArrayList<Pair<Job, T>>()
    private val mutex = Mutex()

    /**
     * 删除数据
     */
    suspend fun removeFirst(value: T): Boolean {
        mutex.withLock {
            val pair = list.find {
                value == it.second
            } ?: return false
            pair.first.cancel()
            list.remove(pair)
        }
        return true
    }

    suspend fun removeAll(value: T): Boolean {
        return mutex.withLock {
            val pairs = list.filter {
                value == it.second
            }
            pairs.forEach { pair ->
                pair.first.cancel()
                list.remove(pair)
            }
            pairs.isNotEmpty()
        }
    }

    /**
     * 清除数据
     */
    suspend fun clear() {
        mutex.withLock {
            list.forEach {
                it.first.cancel()
            }
            list.clear()
        }
    }

    /**
     * 增加数据
     */
    suspend fun add(value: T) {
        mutex.withLock {
            lateinit var pair: Pair<Job, T>
            pair = coroutineScope.launch {
                delay(downTime)
                mutex.withLock {
                    list.remove(pair)
                }
            } to value
            list.add(pair)
        }
    }

    /**
     * 是否包含相应数据
     */
    suspend fun contains(value: T): Boolean {
        return mutex.withLock {
            list.find {
                value == it.second
            } != null
        }
    }

    /**
     * 转为List(copy一份)
     */
    suspend fun toList(): List<T> {
        return mutex.withLock {
            list.map { it.second }
        }
    }
}