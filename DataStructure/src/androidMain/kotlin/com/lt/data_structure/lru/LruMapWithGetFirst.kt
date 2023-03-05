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

import java.util.concurrent.atomic.AtomicLong

/**
 * creator: lt  2020/12/19  lt.dygzs@qq.com
 * effect : Lru算法的Map,get方法效率较put高
 *          基于hash表+数组+链表,且线程安全
 *          可用于如用户UserBean,分id的锁(提升锁效率)
 * warning: get的时候使lruId++并赋值,put的时候检查当前是否已经满了,如果满了就遍历移除最小的id
 *
 * [K]表示键的泛型,[V]表示值的泛型
 * [maxSize]最多能存储多少的数据,需要注意maxSize要大于1,否则抛异常
 * [loadFactor]散列比例,越小内部数组就越大,hash碰撞的概率就越小,大于1后必定会发生hash碰撞,参考HashMap,需要注意maxSize要大于0,否则抛异常
 * [valueFactory]如果使用[LruMapWithGetFirst.getOrCreate]则需要使用该方法来创建默认的value对象
 */
actual open class LruMapWithGetFirst<K, V>(
    val maxSize: Int,
    loadFactor: Float = 0.75f,
    val valueFactory: ((key: K) -> V)? = null
) : MutableMap<K, V> {

    init {
        if (maxSize <= 1)
            throw IllegalArgumentException("Illegal max size: $maxSize")
        if (loadFactor <= 0 || java.lang.Float.isNaN(loadFactor))
            throw IllegalArgumentException("Illegal load factor: $loadFactor")
    }

    //数组的大小
    protected val dataMaxSize = (maxSize / loadFactor).toInt()

    //基于数组+链表来存储数据
    protected var data = Array<Node<K, V>?>(dataMaxSize) { null }

    /**
     * 基数lurId,Node中存储的都是基于此的
     */
    protected val mLruId = AtomicLong(Long.MIN_VALUE)

    /**
     * 当前的数量
     */
    override var size = 0

    /**
     * 根据key获取对应的value
     */
    override fun get(key: K): V? = getNode(key)?.value

    /**
     * 获取Node<K,V>
     */
    fun getNode(key: K): Node<K, V>? {
        val justGet = justGet(key)
        justGet?.lruId = mLruId.incrementAndGet()
        return justGet
    }

    /**
     * 先根据key获取对应的value,如果获取不到则创建,需要在构造中传入[valueFactory]
     */
    fun getOrCreate(key: K): V {
        val valueFactory = valueFactory!!
        val node = getNode(key)
        if (node != null)
            return node.value
        val value = valueFactory(key)
        put(key, value)
        return value
    }

    /**
     * 先根据key获取对应的value,如果获取不到则创建,需要在构造中传入[valueFactory],如果再获取不到则复用旧的数据(移除最旧的然后赋值给最新的,相当于替换了key并将优先级提升)
     */
    fun getOrCreateOrMultiplexing(key: K): V {
        val valueFactory = valueFactory!!
        val node = getNode(key)
        if (node != null)
            return node.value
        return if (size < maxSize) {
            val value = valueFactory(key)
            put(key, value)
            value
        } else {
            val multiplexingValue = removeLruLast()
            put(key, multiplexingValue)
            multiplexingValue
        }
    }

    /**
     * 当前数据是否为空
     */
    @Synchronized
    override fun isEmpty(): Boolean = size == 0

    /**
     * 清空所有数据
     */
    @Synchronized
    override fun clear() {
        for (i in 0 until dataMaxSize) {
            data[i] = null
        }
        size = 0
        mLruId.set(Long.MIN_VALUE)
    }

    /**
     * 添加数据,如果其内数据已满且key无相同的,则会移除最早使用的一条数据
     */
    @Synchronized
    override fun put(key: K, value: V): V? {
        val keyHash = hash(key)
        val index = getIndex(keyHash)
        var mThis: Node<K, V>? = data[index]
        var hasKey = false
        while (mThis != null) {
            if (mThis.keyHash == keyHash && mThis.key == key) {
                hasKey = true
                break
            }
            mThis = mThis.next
        }
        if (mThis != null && hasKey) {
            val oldValue = mThis.value
            mThis.value = value
            mThis.lruId = mLruId.incrementAndGet()
            return oldValue
        }
        //上面为有原key,替换掉value,不修改位置.下面为新key,添加到新位置中
        mThis = data[index]
        if (mThis == null) {
            data[index] = Node(key, value, keyHash, mLruId.incrementAndGet(), null)
        } else {
            while (mThis?.next != null) {
                mThis = mThis.next
            }
            mThis!!.next = Node(key, value, keyHash, mLruId.incrementAndGet(), null)
        }
        size++
        if (size > maxSize) {
            removeLruLast()
        }
        return null
    }

    /**
     * 移除lru算法下优先级最低的数据
     */
    fun removeLruLast(): V {
        if (size <= 0)
            throw NoSuchElementException()
        var prev: Node<K, V>? = null
        var thiz: Node<K, V>? = null
        var id = Long.MAX_VALUE
        forEachData { mPrev, mThis ->
            if (id >= mThis.lruId) {
                id = mThis.lruId
                prev = mPrev
                thiz = mThis
            }
        }
        val mThis = thiz!!
        if (prev == null) {
            data[getIndex(mThis.key)] = mThis.next
        } else {
            prev!!.next = mThis.next
        }
        size--
        return mThis.value
    }

    /**
     * 移除相应的key,并返回相应的value
     */
    @Synchronized
    override fun remove(key: K): V? {
        val keyHash = hash(key)
        val index = getIndex(keyHash)
        var next = data[index]
        var prev: Node<K, V>? = null
        while (next != null) {
            if (next.keyHash == keyHash && next.key == key) {
                if (prev == null) {
                    data[index] = next.next
                } else {
                    prev.next = next.next
                }
                size--
                return next.value
            }
            prev = next
            next = next.next
        }
        return null
    }

    /**
     * 将传入的数据全部提交到LruMap中,若传入的map大于LruMap的maxSize,则部分数据无意义
     */
    @Synchronized
    override fun putAll(from: Map<out K, V>) {
        from.forEach {
            put(it.key, it.value)
        }
    }

    /**
     * 获取对象的hash值
     */
    protected open fun hash(any: Any?): Int = any?.hashCode() ?: 0

    /**
     * 根据hash和maxSize获取索引
     */
    @Synchronized
    protected open fun getIndex(hash: Int): Int = hash % dataMaxSize
    protected open fun getIndex(any: Any?): Int = getIndex(hash(any))

    /**
     * 只获取,不影响Lru算法
     */
    @Synchronized
    open fun justGet(key: K): Node<K, V>? {
        val keyHash = hash(key)
        var next = data[getIndex(keyHash)]
        while (next != null) {
            if (next.keyHash == keyHash && next.key == key)
                return next
            next = next.next
        }
        return null
    }

    /**
     * 获取数据集,消耗较大
     */
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        @Synchronized
        get() {
            val set = HashSet<MutableMap.MutableEntry<K, V>>(size)
            forEachData { prev, mThis ->
                set.add(mThis.toEntry())
            }
            return set
        }

    /**
     * 遍历数据
     */
    protected inline fun forEachData(action: (prev: Node<K, V>?, mThis: Node<K, V>) -> Unit) {
        data.forEach {
            var next = it
            var prev: Node<K, V>? = null
            while (next != null) {
                action(prev, next)
                prev = next
                next = next.next
            }
        }
    }

    /**
     * 获取keys
     */
    override val keys: MutableSet<K>
        @Synchronized
        get() {
            val list = HashSet<K>(size)
            data.forEach {
                var next = it
                while (next != null) {
                    list.add(next.key)
                    next = next.next
                }
            }
            return list
        }

    /**
     * 获取values
     */
    override val values: MutableCollection<V>
        @Synchronized
        get() {
            val list = ArrayList<V>(size)
            data.forEach {
                var next = it
                while (next != null) {
                    list.add(next.value)
                    next = next.next
                }
            }
            return list
        }

    /**
     * 是否存在这个key
     */
    override fun containsKey(key: K): Boolean = justGet(key) != null


    /**
     * 是否存在这个Value,效率较低
     */
    override fun containsValue(value: V): Boolean {
        val keyHash = hash(value)
        data.forEach {
            var next = it
            while (next != null) {
                if (next.keyHash == keyHash && value == next.value)
                    return true
                next = next.next
            }
        }
        return false
    }

    /**
     * 存储数据的节点
     * [key]键
     * [value]值
     * [keyHash]键的hash
     * [lruId]用于Lru算法的计算
     * [next]用于hash冲突后的链式存储
     */
    class Node<K, V>(
        override val key: K,
        override var value: V,
        val keyHash: Int,
        var lruId: Long,
        var next: Node<K, V>?
    ) : MutableMap.MutableEntry<K, V> {

        /**
         * 设置新值,并返回旧值
         */
        override fun setValue(newValue: V): V {
            val oldValue = value
            value = newValue
            return oldValue
        }

        fun toEntry() = Entry(key, value)

        class Entry<K, V>(override val key: K, override val value: V) : MutableMap.MutableEntry<K, V> {
            override fun setValue(newValue: V): V = throw UnsupportedOperationException()
        }
    }
}