package com.lt.data_structure.time

/**
 * creator: lt  2023/7/12  lt.dygzs@qq.com
 * effect : 会根据倒计时自动移除数据的list
 * warning: 内部使用CopyOnWriteArrayList
 *          线程安全
 * [downTime]倒计时多少毫秒自动移除数据
 */
expect class DownTimeList<T>