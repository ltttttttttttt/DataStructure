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

private typealias BasicsBoolean = Boolean

/**
 * creator: lt  2021/11/10  lt.dygzs@qq.com
 * effect : 性能更好的ArrayList<Boolean>,线程不安全
 * warning:[initSize]初始化容量
 * ps:json无法转化成[],但可以调用toString()
 */
class BooleanArrayList(initSize: Int = 10) : RandomAccess {
    constructor(booleanArray: BooleanArray) : this(booleanArray.size) {
        data = booleanArray.copyOf()
        size = data.size
    }

    constructor(booleanArrayList: BooleanArrayList) : this(booleanArrayList.data.copyOf(booleanArrayList.size))

    constructor(list: Collection<BasicsBoolean>) : this(list.size) {
        list.forEach(::add)
    }

    //内部数据
    private var data: BooleanArray = BooleanArray(initSize) { false }

    /**
     * 获取内部的总数量
     */
    var size: Int = 0
        private set

    /**
     * 获取数据
     */
    operator fun get(index: Int): BasicsBoolean {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        return data[index]
    }

    /**
     * 获取数据,如果索引越界,就返回else的返回值
     */
    inline fun getOrElse(index: Int, defaultValue: () -> BasicsBoolean): BasicsBoolean {
        if (index !in 0 until size)
            return defaultValue()
        return get(index)
    }

    fun getOrElse(index: Int, defaultValue: BasicsBoolean): BasicsBoolean {
        if (index !in 0 until size)
            return defaultValue
        return get(index)
    }

    /**
     * 获取数据,如果索引越界,就返回null
     */
    fun getOrNull(index: Int): BasicsBoolean? {
        if (index !in 0 until size)
            return null
        return get(index)
    }

    /**
     * 添加数据
     * 扩容机制:容量翻倍
     */
    fun add(element: BasicsBoolean) {
        if (size == data.size)
            data = data.copyOf(data.size * 2)
        data[size] = element
        size++
    }

    /**
     * 根据数据移除
     */
    fun removeElement(element: BasicsBoolean) {
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
    operator fun set(index: Int, element: BasicsBoolean): BasicsBoolean {
        if (index >= size)
            throw IndexOutOfBoundsException("size = $size ,the index = $index")
        val oldElement = get(index)
        data[index] = element
        return oldElement
    }

    /**
     * 如果[index]没有超过size就设置,否则丢弃该次修改
     */
    fun setOrDiscard(index: Int, element: BasicsBoolean) {
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
    fun indexOf(element: BasicsBoolean): Int {
        forEachIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 从后往前获取对应数据的索引,如果没有则返回-1
     */
    fun lastIndexOf(element: BasicsBoolean): Int {
        forEachReversedIndexed { index, datum ->
            if (element == datum)
                return index
        }
        return -1
    }

    /**
     * 获取是否存在对应数据
     */
    operator fun contains(element: BasicsBoolean): Boolean = indexOf(element) >= 0

    /**
     * 获取迭代器
     */
    operator fun iterator(): MutableIterator<BasicsBoolean> = object : MutableIterator<BasicsBoolean> {
        private var index = 0
        override fun hasNext(): Boolean = size > index
        override fun next(): BasicsBoolean = get(index++)
        override fun remove() = removeAtIndex(--index)
    }

    /**
     * 遍历的方法
     * ps:使用forEach系列比for性能好(因为迭代器的next()返回的是对象)
     */
    inline fun forEach(action: (element: BasicsBoolean) -> Unit) {
        forEachIndexed { _, element -> action(element) }
    }

    inline fun forEachIndexed(action: (index: Int, element: BasicsBoolean) -> Unit) {
        var index = 0
        while (index < size) {
            action(index, get(index))
            index++
        }
    }

    /**
     * 倒序遍历
     */
    inline fun forEachReversedIndexed(action: (index: Int, element: BasicsBoolean) -> Unit) {
        var index = size - 1
        while (index >= 0) {
            action(index, get(index))
            index--
        }
    }

    /**
     * 获取一段BooleanArrayList
     */
    fun subList(fromIndex: Int, toIndex: Int): BooleanArrayList {
        if (toIndex > size)
            throw IndexOutOfBoundsException("size = $size ,the toIndex = $toIndex")
        return BooleanArrayList(data.copyOfRange(fromIndex, toIndex))
    }

    /**
     * 安全的subList,索引超限部分不会返回内容
     */
    fun subListWithSafe(fromIndex: Int, toIndex: Int): BooleanArrayList =
        BooleanArrayList(data.copyOfRange(maxOf(0, fromIndex), minOf(size, toIndex)))

    /**
     * 批量添加数据
     */
    fun addAll(elements: Collection<BasicsBoolean>) {
        elements.forEach(::add)
    }

    fun addAll(elements: BooleanArrayList) {
        addAll(elements.data.copyOf(elements.size))
    }

    fun addAll(elements: BooleanArray) {
        elements.forEach(::add)
    }

    fun addAllNotNull(elements: Collection<BasicsBoolean?>?) {
        elements?.forEach {
            if (it != null)
                add(it)
        }
    }

    /**
     * 批量移除数据
     */
    fun removeAll(elements: Collection<BasicsBoolean>) {
        elements.forEach(::removeElement)
    }

    fun removeAll(elements: BooleanArrayList) {
        removeAll(elements.data.copyOf(elements.size))
    }

    fun removeAll(elements: BooleanArray) {
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
    fun toBooleanArray() = data.copyOf(size)

    fun toMutableList() = toBooleanArray().toMutableList()

    override fun toString(): String {
        return "[" + toBooleanArray().joinToString(",") + "]"
    }
}

fun booleanArrayListOf(vararg elements: BasicsBoolean): BooleanArrayList = BooleanArrayList(elements)