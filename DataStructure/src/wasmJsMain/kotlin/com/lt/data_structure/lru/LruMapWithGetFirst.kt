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
actual open class LruMapWithGetFirst<K, V>