/*
 * Copyright lt 2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lt.data_structure.time

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * creator: lt  2022/7/6  lt.dygzs@qq.com
 * effect : 会根据倒计时自动移除数据的map
 * warning: 内部使用ConcurrentHashMap
            线程安全
 * [downTime]倒计时多少毫秒自动移除数据
 */
actual class DownTimeMap<K : Any, V>(
    private val downTime: Long,
    private val coroutineScope: CoroutineScope,
) {
    private val map = ConcurrentHashMap<K, Pair<Job, V>>()

    /**
     * 添加数据
     * [downTime]手动定义这一条数据的时间
     */
    fun put(key: K, value: V, downTime: Long = this.downTime) {
        map.remove(key)?.first?.cancel()
        map[key] = coroutineScope.launch {
            delay(downTime)
            remove(key)
        } to value
    }

    operator fun set(key: K, value: V) = put(key, value)

    /**
     * 获取数据
     */
    operator fun get(key: K): V? = map[key]?.second

    /**
     * 删除数据
     */
    fun remove(key: K): V? {
        val pair = map.remove(key)
        pair?.first?.cancel()
        return pair?.second
    }

    /**
     * 获取key的集合
     */
    val keys: List<K>
        get() = map.keys.toList()

    /**
     * 获取value集合
     */
    val values: List<V>
        get() = map.values.map { it.second }
}