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

package com.lt.data_structure.basic_value

private typealias BasicsLong = Long

/**
 * creator: lt  2021/11/10  lt.dygzs@qq.com
 * effect : 性能更好的ArrayList<Long>,线程不安全
 * warning:[initSize]初始化容量
 * ps:json无法转化成[],但可以调用toString()
 */
class LongArrayList(initSize: Int = 10) : RandomAccess {
    constructor(longArray: LongArray) : this(longArray.size) {
        data = longArray.copyOf()
        size = data.size
    }

    constructor(longArrayList: LongArrayList) : this(longArrayList.data.copyOf(longArrayList.size))

    constructor(list: Collection<BasicsLong>) : this(list.size) {
        list.forEach(::add)
    }

    //内部数据
    private var data: LongArray = LongArray(initSize) { 0L }

    /**
     * 获取内部的总数量
     */
    var size: Int = 0
        private set

    /**
     * 获取数据
     */
    operator fun get(index: Int): BasicsLong {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        return data[index]
    }

    /**
     * 获取数据,如果索引越界,就返回else的返回值
     */
    inline fun getOrElse(index: Int, defaultValue: () -> BasicsLong): BasicsLong {
        if (index !in 0 until size)
            return defaultValue()
        return get(index)
    }

    fun getOrElse(index: Int, defaultValue: BasicsLong): BasicsLong {
        if (index !in 0 until size)
            return defaultValue
        return get(index)
    }

    /**
     * 获取数据,如果索引越界,就返回null
     */
    fun getOrNull(index: Int): BasicsLong? {
        if (index !in 0 until size)
            return null
        return get(index)
    }

    /**
     * 添加数据
     * 扩容机制:容量翻倍
     */
    fun add(element: BasicsLong) {
        if (size == data.size)
            data = data.copyOf(data.size * 2)
        data[size] = element
        size++
    }

    /**
     * 根据数据移除
     */
    fun removeElement(element: BasicsLong) {
        val indexOf = indexOf(element)
        if (indexOf >= 0) {
            removeAtIndex(indexOf)
        }
    }

    /**
     * 根据索引移除
     */
    fun removeAtIndex(index: Int) {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        val numMoved = size - index - 1
        if (numMoved > 0)
            System.arraycopy(data, index + 1, data, index, numMoved)
        size--
    }

    /**
     * 移除第一个位置
     */
    fun removeFirst() = removeAtIndex(0)

    /**
     * 移除最后一个位置
     */
    fun removeLast() = removeAtIndex(size - 1)

    /**
     * 设置某个索引的数据
     */
    operator fun set(index: Int, element: BasicsLong): BasicsLong {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        val oldElement = get(index)
        data[index] = element
        return oldElement
    }

    /**
     * 如果[index]没有超过size就设置,否则丢弃该次修改
     */
    fun setOrDiscard(index: Int, element: BasicsLong) {
        if (index >= size || index < 0) return
        set(index, element)
    }

    /**
     * 获取内部是否没有数据
     */
    fun isEmpty(): Boolean = size == 0

    /**
     * 获取对应数据的索引,如果没有则返回-1
     */
    fun indexOf(element: BasicsLong): Int {
        forEachIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 从后往前获取对应数据的索引,如果没有则返回-1
     */
    fun lastIndexOf(element: BasicsLong): Int {
        forEachReversedIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 获取是否存在对应数据
     */
    operator fun contains(element: BasicsLong): Boolean = indexOf(element) >= 0

    /**
     * 获取迭代器
     */
    operator fun iterator(): MutableIterator<BasicsLong> = object : MutableIterator<BasicsLong> {
        private var index = 0
        override fun hasNext(): Boolean = size > index
        override fun next(): BasicsLong = get(index++)
        override fun remove() = removeAtIndex(--index)
    }

    /**
     * 遍历的方法
     * ps:使用forEach系列比for性能好(因为迭代器的next()返回的是对象)
     */
    inline fun forEach(action: (element: BasicsLong) -> Unit) {
        forEachIndexed { _, element -> action(element) }
    }

    inline fun forEachIndexed(action: (index: Int, element: BasicsLong) -> Unit) {
        var index = 0
        while (index < size) {
            action(index, get(index))
            index++
        }
    }

    /**
     * 倒序遍历
     */
    inline fun forEachReversedIndexed(action: (index: Int, element: BasicsLong) -> Unit) {
        var index = size - 1
        while (index >= 0) {
            action(index, get(index))
            index--
        }
    }

    /**
     * 获取一段LongArrayList
     */
    fun subList(fromIndex: Int, toIndex: Int): LongArrayList {
        if (toIndex > size)
            throw IndexOutOfBoundsException("size = $size ,the toIndex = $toIndex")
        return LongArrayList(data.copyOfRange(fromIndex, toIndex))
    }

    /**
     * 安全的subList,索引超限部分不会返回内容
     */
    fun subListWithSafe(fromIndex: Int, toIndex: Int): LongArrayList =
        LongArrayList(data.copyOfRange(maxOf(0, fromIndex), minOf(size, toIndex)))

    /**
     * 批量添加数据
     */
    fun addAll(elements: Collection<BasicsLong>) {
        elements.forEach(::add)
    }

    fun addAll(elements: LongArrayList) {
        addAll(elements.data.copyOf(elements.size))
    }

    fun addAll(elements: LongArray) {
        elements.forEach(::add)
    }

    fun addAllNotNull(elements: Collection<BasicsLong?>?) {
        elements?.forEach {
            if (it != null)
                add(it)
        }
    }

    /**
     * 批量移除数据
     */
    fun removeAll(elements: Collection<BasicsLong>) {
        elements.forEach(::removeElement)
    }

    fun removeAll(elements: LongArrayList) {
        removeAll(elements.data.copyOf(elements.size))
    }

    fun removeAll(elements: LongArray) {
        elements.forEach(::removeElement)
    }

    /**
     * 清空数据
     */
    fun clear() {
        size = 0
    }

    /**
     * 转换数据结构
     */
    fun toLongArray() = data.copyOf(size)

    fun toMutableList() = toLongArray().toMutableList()

    override fun toString(): String {
        return "[" + toLongArray().joinToString(",") + "]"
    }
}

fun longArrayListOf(vararg elements: BasicsLong): LongArrayList = LongArrayList(elements)