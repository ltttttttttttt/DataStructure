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

package com.lt.data_structure.sorted

/**
 * creator: lt  2021/12/1  lt.dygzs@qq.com
 * effect : 排序且去重的List,线程不安全
 * warning: 其他地方不要持有内部的[list],并且[list]要是个空的(或者是按照构造里的规则有序且去重的)
 * [equalsFun] 判断两个数据是否相同的方法:添加,删除和比较都会使用此方法,默认使用equals
 * [repeatIsReplace] 重复后是否进行替换旧数据操作,默认是,否则丢弃添加的数据
 * [list] 内部存储数据的list,如果使用ArrayList则是查询快,增平,删慢;使用LinkedList是删快,增平,查询慢;
 *          add方法会根据list的类型采用不同方式减少时间,支持随机读取使用二分法,不支持使用遍历法
 * [sortFun] 排序方法,返回:大于0表示t1大,等于0表示一样大,小于0表示t2大,ps:如果判断的数值使用的Long的话,最好是判断一下是否大于小于0并返回1或0或-1,防止强转成Int溢出
 */
class SortedAndNotRepeatList<T>(
    private val equalsFun: (t: T, t2: T) -> Boolean = { t, t2 -> t == t2 },
    private val repeatIsReplace: Boolean = true,
    private val list: MutableList<T> = ArrayList(),
    private val sortFun: (t: T, t2: T) -> Int
) : MutableList<T> by list {
    override fun contains(element: T): Boolean = find { equalsFun(element, it) } != null

    override fun containsAll(elements: Collection<T>): Boolean = elements.all(::contains)

    override fun indexOf(element: T): Int = indexOfFirst { equalsFun(element, it) }

    override fun lastIndexOf(element: T): Int = indexOfLast { equalsFun(element, it) }

    override fun add(element: T): Boolean {
        if (repeatIsReplace) {
            remove(element)
        } else {
            if (contains(element))
                return false
        }
        if (list is RandomAccess) {
            binarySortAdd(element)
        } else {
            foreachSortAdd(element)
        }
        return true
    }

    /**
     * 二分排序法添加数据,适用于支持随机读取的list
     */
    private fun binarySortAdd(element: T) {
        if (size == 0) {
            list.add(element)
            return
        }
        if (sortFun(element, first()) < 0) {
            list.add(0, element)
            return
        }
        if (sortFun(element, last()) >= 0) {
            list.add(element)
            return
        }

        var min = 0
        var max = size
        var mid = max / 2 + min
        while (true) {
            val sort = sortFun(element, get(mid))
            if (sort < 0) {
                if (min >= mid) {
                    list.add(mid, element)
                    return
                }
                if (max == mid) {
                    max--
                    mid--
                } else {
                    max = mid
                    mid -= (mid - min) / 2
                }
            } else if (sort == 0) {
                mid++
                min = mid
            } else if (sort > 0) {
                if (max <= mid) {
                    list.add(mid + 1, element)
                    return
                }
                if (min == mid) {
                    min++
                    mid++
                } else {
                    min = mid
                    mid += (max - mid) / 2
                }
            }
        }
    }

    /**
     * 遍历排序法插入,适用于不支持随机读取的list
     */
    private fun foreachSortAdd(element: T) {
        forEachIndexed { index, t ->
            if (sortFun(element, t) < 0) {
                list.add(index, element)
                return
            }
        }
        list.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(::add)
        return true
    }

    override fun remove(element: T): Boolean {
        val indexOf = indexOf(element)
        if (indexOf < 0)
            return false
        removeAt(indexOf)
        return true
    }

    override fun removeAll(elements: Collection<T>): Boolean = elements.all(::remove)

    override fun retainAll(elements: Collection<T>): Boolean {
        clear()
        elements.forEach(::add)
        return true
    }

    @Deprecated("由于该类是有序的,所以带有index的add和set方法无效", level = DeprecationLevel.ERROR)
    override fun add(index: Int, element: T) =
        throw RuntimeException("由于该类是有序的,所以带有index的add和set方法无效")

    @Deprecated("由于该类是有序的,所以带有index的add和set方法无效", level = DeprecationLevel.ERROR)
    override fun addAll(index: Int, elements: Collection<T>): Boolean =
        throw RuntimeException("由于该类是有序的,所以带有index的add和set方法无效")

    @Deprecated("由于该类是有序的,所以带有index的add和set方法无效", level = DeprecationLevel.ERROR)
    override fun set(index: Int, element: T): T =
        throw RuntimeException("由于该类是有序的,所以带有index的add和set方法无效")
}