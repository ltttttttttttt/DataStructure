package com.lt.data_structure.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.CopyOnWriteArrayList

/**
 * creator: lt  2023/7/12  lt.dygzs@qq.com
 * effect : 会根据倒计时自动移除数据的list
 * warning: 内部使用CopyOnWriteArrayList
 *          线程安全
 * [downTime]倒计时多少毫秒自动移除数据
 */
actual class DownTimeList<T>(
    private val downTime: Long,
    private val coroutineScope: CoroutineScope
) {
    private val list = CopyOnWriteArrayList<Pair<Job, T>>()

    /**
     * 删除数据
     */
    fun removeFirst(value: T): Boolean {
        val pair = list.find {
            value == it.second
        } ?: return false
        pair.first.cancel()
        list.remove(pair)
        return true
    }

    fun removeAll(value: T): Boolean {
        val pairs = list.filter {
            value == it.second
        }
        pairs.forEach { pair ->
            pair.first.cancel()
            list.remove(pair)
        }
        return pairs.isNotEmpty()
    }

    /**
     * 清除数据
     */
    fun clear() {
        list.forEach {
            it.first.cancel()
        }
        list.clear()
    }

    /**
     * 增加数据
     */
    fun add(value: T) {
        lateinit var pair: Pair<Job, T>
        pair = coroutineScope.launch {
            delay(downTime)
            list.remove(pair)
        } to value
        list.add(pair)
    }

    /**
     * 是否包含相应数据
     */
    fun contains(value: T): Boolean {
        return list.find {
            value == it.second
        } != null
    }

    /**
     * 转为List(copy一份)
     */
    fun toList(): List<T> {
        return list.map { it.second }
    }
}