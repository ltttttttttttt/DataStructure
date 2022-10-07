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

private typealias BasicsDouble = Double

/**
 * creator: lt  2021/11/10  lt.dygzs@qq.com
 * effect : 性能更好的ArrayList<Double>,线程不安全
 * warning:[initSize]初始化容量
 * ps:json无法转化成[],但可以调用toString()
 */
class DoubleArrayList(initSize: Int = 10) : RandomAccess {
    constructor(doubleArray: DoubleArray) : this(doubleArray.size) {
        data = doubleArray.copyOf()
        size = data.size
    }

    constructor(doubleArrayList: DoubleArrayList) : this(doubleArrayList.data.copyOf(doubleArrayList.size))

    constructor(list: Collection<BasicsDouble>) : this(list.size) {
        list.forEach(::add)
    }

    //内部数据
    private var data: DoubleArray = DoubleArray(initSize) { 0.0 }

    /**
     * 获取内部的总数量
     */
    var size: Int = 0
        private set

    /**
     * 获取数据
     */
    operator fun get(index: Int): BasicsDouble {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        return data[index]
    }

    /**
     * 获取数据,如果索引越界,就返回else的返回值
     */
    inline fun getOrElse(index: Int, defaultValue: () -> BasicsDouble): BasicsDouble {
        if (index !in 0 until size)
            return defaultValue()
        return get(index)
    }

    fun getOrElse(index: Int, defaultValue: BasicsDouble): BasicsDouble {
        if (index !in 0 until size)
            return defaultValue
        return get(index)
    }

    /**
     * 获取数据,如果索引越界,就返回null
     */
    fun getOrNull(index: Int): BasicsDouble? {
        if (index !in 0 until size)
            return null
        return get(index)
    }

    /**
     * 添加数据
     * 扩容机制:容量翻倍
     */
    fun add(element: BasicsDouble) {
        if (size == data.size)
            data = data.copyOf(data.size * 2)
        data[size] = element
        size++
    }

    /**
     * 根据数据移除
     */
    fun removeElement(element: BasicsDouble) {
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
    operator fun set(index: Int, element: BasicsDouble): BasicsDouble {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        val oldElement = get(index)
        data[index] = element
        return oldElement
    }

    /**
     * 如果[index]没有超过size就设置,否则丢弃该次修改
     */
    fun setOrDiscard(index: Int, element: BasicsDouble) {
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
    fun indexOf(element: BasicsDouble): Int {
        forEachIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 从后往前获取对应数据的索引,如果没有则返回-1
     */
    fun lastIndexOf(element: BasicsDouble): Int {
        forEachReversedIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 获取是否存在对应数据
     */
    operator fun contains(element: BasicsDouble): Boolean = indexOf(element) >= 0

    /**
     * 获取迭代器
     */
    operator fun iterator(): MutableIterator<BasicsDouble> = object : MutableIterator<BasicsDouble> {
        private var index = 0
        override fun hasNext(): Boolean = size > index
        override fun next(): BasicsDouble = get(index++)
        override fun remove() = removeAtIndex(--index)
    }

    /**
     * 遍历的方法
     * ps:使用forEach系列比for性能好(因为迭代器的next()返回的是对象)
     */
    inline fun forEach(action: (element: BasicsDouble) -> Unit) {
        forEachIndexed { _, element -> action(element) }
    }

    inline fun forEachIndexed(action: (index: Int, element: BasicsDouble) -> Unit) {
        var index = 0
        while (index < size) {
            action(index, get(index))
            index++
        }
    }

    /**
     * 倒序遍历
     */
    inline fun forEachReversedIndexed(action: (index: Int, element: BasicsDouble) -> Unit) {
        var index = size - 1
        while (index >= 0) {
            action(index, get(index))
            index--
        }
    }

    /**
     * 获取一段DoubleArrayList
     */
    fun subList(fromIndex: Int, toIndex: Int): DoubleArrayList {
        if (toIndex > size)
            throw IndexOutOfBoundsException("size = $size ,the toIndex = $toIndex")
        return DoubleArrayList(data.copyOfRange(fromIndex, toIndex))
    }

    /**
     * 安全的subList,索引超限部分不会返回内容
     */
    fun subListWithSafe(fromIndex: Int, toIndex: Int): DoubleArrayList =
        DoubleArrayList(data.copyOfRange(maxOf(0, fromIndex), minOf(size, toIndex)))

    /**
     * 批量添加数据
     */
    fun addAll(elements: Collection<BasicsDouble>) {
        elements.forEach(::add)
    }

    fun addAll(elements: DoubleArrayList) {
        addAll(elements.data.copyOf(elements.size))
    }

    fun addAll(elements: DoubleArray) {
        elements.forEach(::add)
    }

    fun addAllNotNull(elements: Collection<BasicsDouble?>?) {
        elements?.forEach {
            if (it != null)
                add(it)
        }
    }

    /**
     * 批量移除数据
     */
    fun removeAll(elements: Collection<BasicsDouble>) {
        elements.forEach(::removeElement)
    }

    fun removeAll(elements: DoubleArrayList) {
        removeAll(elements.data.copyOf(elements.size))
    }

    fun removeAll(elements: DoubleArray) {
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
    fun toDoubleArray() = data.copyOf(size)

    fun toMutableList() = toDoubleArray().toMutableList()

    override fun toString(): String {
        return "[" + toDoubleArray().joinToString(",") + "]"
    }
}

fun doubleArrayListOf(vararg elements: BasicsDouble): DoubleArrayList = DoubleArrayList(elements)