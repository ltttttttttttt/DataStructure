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

package com.lt.data_structure.lru

/**
 * creator: lt  2020/12/19  lt.dygzs@qq.com
 * effect : Lru算法的Map
 *          基于LinkedHashMap,线程不安全
 *          可用于经常提交但比较少获取的情景,或提交和获取相当
 * warning: get的时候将当前数据移除并添加到队列末尾,put的时候检查当前是否已经满了,如果满了就直接移除队列头(这种适合经常put的)
 *
 * [K]表示键的泛型,[V]表示值的泛型
 * [maxSize]最多能存储多少的数据,需要注意maxSize要大于1,否则抛异常
 * [loadFactor]散列比例,越小内部数组就越大,hash碰撞的概率就越小,大于1后必定会发生hash碰撞,参考HashMap,需要注意maxSize要大于0,否则抛异常
 */
actual class LruMap<K, V>(val maxSize: Int, loadFactor: Float = 0.75f)
    : LinkedHashMap<K, V>(16, loadFactor, true) {

    init {
        if (maxSize <= 1)
            throw IllegalArgumentException("Illegal max size: $maxSize")
    }

    override fun put(key: K, value: V): V? {
        if (maxSize <= size)
            removeOldest()
        return super.put(key, value)
    }

    /**
     * 删除最旧的数据
     */
    private fun removeOldest() {
        remove(keys.first())
    }
}