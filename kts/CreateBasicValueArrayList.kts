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

import java.io.File

/**
 * creator: lt  2022/10/7  lt.dygzs@qq.com
 * effect : 创建基础类型的ArrayList的脚本
 * warning:
 */

//需要被生成的基础类型
val basicValues = listOf(
    BasicInfo("Int", "0"),
    BasicInfo("Long", "0L"),
    BasicInfo("Float", "0f"),
    BasicInfo("Double", "0.0"),
    BasicInfo("Boolean", "false"),
)

//生成文件的目录
val cacheDir = File("../build/cache")
if (!cacheDir.isDirectory) {
    cacheDir.mkdirs()
}

//生成kt文件
basicValues.forEach { basicInfo ->
    val file = File(cacheDir, basicInfo.name + "ArrayList.kt")
    if (file.exists())
        throw RuntimeException("文件已存在")
    file.createNewFile()
    createKtFile(basicInfo, file)
}

class BasicInfo(
    val name: String,
    val defalutValue: String,
)

fun createKtFile(basicInfo: BasicInfo, file: File) {
    val basic = basicInfo.name
    val lowerBasic = basic.lowercase()
    file.writeText(
        "/*\n" +
                " * Copyright lt 2023\n" +
                " *\n" +
                " * Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                " * you may not use this file except in compliance with the License.\n" +
                " * You may obtain a copy of the License at\n" +
                " *\n" +
                " *        http://www.apache.org/licenses/LICENSE-2.0\n" +
                " *\n" +
                " * Unless required by applicable law or agreed to in writing, software\n" +
                " * distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                " * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                " * See the License for the specific language governing permissions and\n" +
                " * limitations under the License.\n" +
                " */\n\n" +
                "package com.lt.data_structure.basic_value\n" +
                "\n" +
                "private typealias Basic${basic} = ${basic}\n" +
                "\n" +
                "/**\n" +
                " * creator: lt  2021/11/10  lt.dygzs@qq.com\n" +
                " * effect : 性能更好的ArrayList<${basic}>,线程不安全\n" +
                " * warning:[initSize]初始化容量\n" +
                " * ps:json无法转化成[],但可以调用toString()\n" +
                " */\n" +
                "class ${basic}ArrayList(initSize: Int = 10) : RandomAccess {\n" +
                "    constructor(${lowerBasic}Array: ${basic}Array) : this(${lowerBasic}Array.size) {\n" +
                "        data = ${lowerBasic}Array.copyOf()\n" +
                "        size = data.size\n" +
                "    }\n" +
                "\n" +
                "    constructor(${lowerBasic}ArrayList: ${basic}ArrayList) : this(${lowerBasic}ArrayList.data.copyOf(${lowerBasic}ArrayList.size))\n" +
                "\n" +
                "    constructor(list: Collection<Basic${basic}>) : this(list.size) {\n" +
                "        list.forEach(::add)\n" +
                "    }\n" +
                "\n" +
                "    //内部数据\n" +
                "    private var data: ${basic}Array = ${basic}Array(initSize) { ${basicInfo.defalutValue} }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取内部的总数量\n" +
                "     */\n" +
                "    var size: Int = 0\n" +
                "        private set\n" +
                "\n" +
                "    /**\n" +
                "     * 获取数据\n" +
                "     */\n" +
                "    operator fun get(index: Int): Basic${basic} {\n" +
                "        if (index >= size)\n" +
                "            throw IndexOutOfBoundsException(\"size = \$size ,the index = \$index\")\n" +
                "        return data[index]\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取数据,如果索引越界,就返回else的返回值\n" +
                "     */\n" +
                "    inline fun getOrElse(index: Int, defaultValue: () -> Basic${basic}): Basic${basic} {\n" +
                "        if (index !in 0 until size)\n" +
                "            return defaultValue()\n" +
                "        return get(index)\n" +
                "    }\n" +
                "\n" +
                "    fun getOrElse(index: Int, defaultValue: Basic${basic}): Basic${basic} {\n" +
                "        if (index !in 0 until size)\n" +
                "            return defaultValue\n" +
                "        return get(index)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取数据,如果索引越界,就返回null\n" +
                "     */\n" +
                "    fun getOrNull(index: Int): Basic${basic}? {\n" +
                "        if (index !in 0 until size)\n" +
                "            return null\n" +
                "        return get(index)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 添加数据\n" +
                "     * 扩容机制:容量翻倍\n" +
                "     */\n" +
                "    fun add(element: Basic${basic}) {\n" +
                "        if (size == data.size)\n" +
                "            data = data.copyOf(data.size * 2)\n" +
                "        data[size] = element\n" +
                "        size++\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 根据数据移除\n" +
                "     */\n" +
                "    fun removeElement(element: Basic${basic}) {\n" +
                "        val indexOf = indexOf(element)\n" +
                "        if (indexOf >= 0) {\n" +
                "            removeAtIndex(indexOf)\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 根据索引移除\n" +
                "     */\n" +
                "    fun removeAtIndex(index: Int) {\n" +
                "        if (index >= size)\n" +
                "            throw IndexOutOfBoundsException(\"size = \$size ,the index = \$index\")\n" +
                "        //将需要移除的索引的后面数据往前移\n" +
                "        repeat(size - index - 1) {\n" +
                "            //将后面索引的内容前移\n" +
                "            data[it + index] = data[it + index + 1]\n" +
                "        }\n" +
                "        size--\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 移除第一个位置\n" +
                "     */\n" +
                "    fun removeFirst() = removeAtIndex(0)\n" +
                "\n" +
                "    /**\n" +
                "     * 移除最后一个位置\n" +
                "     */\n" +
                "    fun removeLast() = removeAtIndex(size - 1)\n" +
                "\n" +
                "    /**\n" +
                "     * 设置某个索引的数据\n" +
                "     */\n" +
                "    operator fun set(index: Int, element: Basic${basic}): Basic${basic} {\n" +
                "        if (index >= size)\n" +
                "            throw IndexOutOfBoundsException(\"size = \$size ,the index = \$index\")\n" +
                "        val oldElement = get(index)\n" +
                "        data[index] = element\n" +
                "        return oldElement\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 如果[index]没有超过size就设置,否则丢弃该次修改\n" +
                "     */\n" +
                "    fun setOrDiscard(index: Int, element: Basic${basic}) {\n" +
                "        if (index >= size || index < 0) return\n" +
                "        set(index, element)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取内部是否没有数据\n" +
                "     */\n" +
                "    fun isEmpty(): Boolean = size == 0\n" +
                "\n" +
                "    /**\n" +
                "     * 获取对应数据的索引,如果没有则返回-1\n" +
                "     */\n" +
                "    fun indexOf(element: Basic${basic}): Int {\n" +
                "        forEachIndexed { index, datum ->\n" +
                "            if (element == datum)\n" +
                "                return index\n" +
                "        }\n" +
                "        return -1\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 从后往前获取对应数据的索引,如果没有则返回-1\n" +
                "     */\n" +
                "    fun lastIndexOf(element: Basic${basic}): Int {\n" +
                "        forEachReversedIndexed { index, datum ->\n" +
                "            if (element == datum)\n" +
                "                return index\n" +
                "        }\n" +
                "        return -1\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取是否存在对应数据\n" +
                "     */\n" +
                "    operator fun contains(element: Basic${basic}): Boolean = indexOf(element) >= 0\n" +
                "\n" +
                "    /**\n" +
                "     * 获取包装类型迭代器\n" +
                "     */\n" +
                "    operator fun iterator(): ${basic}MutableIterator = ${basic}MutableIterator()\n" +
                "\n" +
                "    /**\n" +
                "     * 获取基础类型迭代器,相对于[iterator]方法,效率更高\n" +
                "     */\n" +
                "    fun iteratorWithBasic(): Basic${basic}MutableIterator = Basic${basic}MutableIterator()\n" +
                "\n" +
                "    /**\n" +
                "     * 遍历的方法,inline后是基础类型,如果无法inline,则使用[forEachWithBasic]系列方法\n" +
                "     * ps:使用forEach系列比for性能好(因为迭代器的next()返回的是对象)\n" +
                "     */\n" +
                "    inline fun forEach(action: (element: Basic${basic}) -> Unit) {\n" +
                "        forEachIndexed { _, element -> action(element) }\n" +
                "    }\n" +
                "\n" +
                "    inline fun forEachIndexed(action: (index: Int, element: Basic${basic}) -> Unit) {\n" +
                "        var index = 0\n" +
                "        while (index < size) {\n" +
                "            action(index, get(index))\n" +
                "            index++\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 倒序遍历\n" +
                "     */\n" +
                "    inline fun forEachReversedIndexed(action: (index: Int, element: Basic${basic}) -> Unit) {\n" +
                "        var index = size - 1\n" +
                "        while (index >= 0) {\n" +
                "            action(index, get(index))\n" +
                "            index--\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 基础类型遍历方法(适用于无法kotlin inline的情况)\n" +
                "     */\n" +
                "    fun forEachWithBasic(action: OnBasic${basic}) {\n" +
                "        var index = 0\n" +
                "        while (index < size) {\n" +
                "            action.onBasic${basic}(get(index))\n" +
                "            index++\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    fun forEachIndexedWithBasic(action: OnBasic${basic}WithIndex) {\n" +
                "        var index = 0\n" +
                "        while (index < size) {\n" +
                "            action.onBasic${basic}WithIndex(index, get(index))\n" +
                "            index++\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 倒序遍历\n" +
                "     */\n" +
                "    fun forEachReversedIndexedWithBasic(action: OnBasic${basic}WithIndex) {\n" +
                "        var index = size - 1\n" +
                "        while (index >= 0) {\n" +
                "            action.onBasic${basic}WithIndex(index, get(index))\n" +
                "            index--\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 获取一段${basic}ArrayList\n" +
                "     */\n" +
                "    fun subList(fromIndex: Int, toIndex: Int): ${basic}ArrayList {\n" +
                "        if (toIndex > size)\n" +
                "            throw IndexOutOfBoundsException(\"size = \$size ,the toIndex = \$toIndex\")\n" +
                "        return ${basic}ArrayList(data.copyOfRange(fromIndex, toIndex))\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 安全的subList,索引超限部分不会返回内容\n" +
                "     */\n" +
                "    fun subListWithSafe(fromIndex: Int, toIndex: Int): ${basic}ArrayList =\n" +
                "        ${basic}ArrayList(data.copyOfRange(maxOf(0, fromIndex), minOf(size, toIndex)))\n" +
                "\n" +
                "    /**\n" +
                "     * 批量添加数据\n" +
                "     */\n" +
                "    fun addAll(elements: Collection<Basic${basic}>) {\n" +
                "        elements.forEach(::add)\n" +
                "    }\n" +
                "\n" +
                "    fun addAll(elements: ${basic}ArrayList) {\n" +
                "        addAll(elements.data.copyOf(elements.size))\n" +
                "    }\n" +
                "\n" +
                "    fun addAll(elements: ${basic}Array) {\n" +
                "        elements.forEach(::add)\n" +
                "    }\n" +
                "\n" +
                "    fun addAllNotNull(elements: Collection<Basic${basic}?>?) {\n" +
                "        elements?.forEach {\n" +
                "            if (it != null)\n" +
                "                add(it)\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 批量移除数据\n" +
                "     */\n" +
                "    fun removeAll(elements: Collection<Basic${basic}>) {\n" +
                "        elements.forEach(::removeElement)\n" +
                "    }\n" +
                "\n" +
                "    fun removeAll(elements: ${basic}ArrayList) {\n" +
                "        removeAll(elements.data.copyOf(elements.size))\n" +
                "    }\n" +
                "\n" +
                "    fun removeAll(elements: ${basic}Array) {\n" +
                "        elements.forEach(::removeElement)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 清空数据\n" +
                "     */\n" +
                "    fun clear() {\n" +
                "        size = 0\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 转换数据结构\n" +
                "     */\n" +
                "    fun to${basic}Array() = data.copyOf(size)\n" +
                "\n" +
                "    fun toMutableList() = to${basic}Array().toMutableList()\n" +
                "\n" +
                "    override fun toString(): String {\n" +
                "        return \"[\" + to${basic}Array().joinToString(\",\") + \"]\"\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 包装类型迭代器\n" +
                "     */\n" +
                "    inner class ${basic}MutableIterator : MutableIterator<Basic${basic}> {\n" +
                "        private var index = 0\n" +
                "        override fun hasNext(): Boolean = size > index\n" +
                "        override fun next(): Basic${basic} = get(index++)\n" +
                "        override fun remove() = removeAtIndex(--index)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 基础类型迭代器\n" +
                "     */\n" +
                "    inner class Basic${basic}MutableIterator {\n" +
                "        private var index = 0\n" +
                "        fun hasNext(): Boolean = size > index\n" +
                "        fun next(): Basic${basic} = get(index++)\n" +
                "        fun remove() = removeAtIndex(--index)\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * 基础类型的lambda\n" +
                "     */\n" +
                "    fun interface OnBasic${basic} {\n" +
                "        fun onBasic${basic}(basic${basic}: Basic${basic})\n" +
                "    }\n" +
                "\n" +
                "    fun interface OnBasic${basic}WithIndex {\n" +
                "        fun onBasic${basic}WithIndex(index: Int, basic${basic}: Basic${basic})\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "fun ${lowerBasic}ArrayListOf(vararg elements: Basic${basic}): ${basic}ArrayList = ${basic}ArrayList(elements)"
    )
}
