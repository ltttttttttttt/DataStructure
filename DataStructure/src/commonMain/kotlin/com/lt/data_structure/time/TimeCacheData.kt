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

import com.lt.data_structure.util._currentTimeMillis
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * creator: lt  2022/2/25  lt.dygzs@qq.com
 * effect : 在服务器中有时效性的数据内存缓存
 * warning:
 */
open class TimeCacheData<T>(
    var cacheTime: Int = 60,//缓存的时效,单位分钟
) {
    private var data: T? = null
    private var time = 0L
    private val mutex = Mutex()

    /**
     * 获取数据,如果时效已过,就会执行传入的lambda
     */
    suspend fun getData(newDataLambda: suspend () -> T): T = mutex.withLock {
        checkAndGetDataWithNoMutex(newDataLambda)
    }

    /**
     * 获取数据,如果时效已过,就会执行[newDataLambda]
     * 会在获取数据后走[gotBlockWithMutex] (是加了协程锁的),然后将其的返回值返回出去
     * ps:该方法主要是将协程锁的作用域延长到了获取数据后的lambda中
     */
    suspend fun <U> getDataWithMutex(newDataLambda: suspend () -> T, gotBlockWithMutex: suspend (T) -> U): U =
        mutex.withLock {
            gotBlockWithMutex(checkAndGetDataWithNoMutex(newDataLambda))
        }

    /**
     * 清除时效和缓存的内容
     * [beforeClear]可以在清除之前做一些事情,且有协程锁的作用域
     */
    suspend fun clearData(beforeClear: (suspend () -> Unit)? = null) {
        mutex.withLock {
            beforeClear?.invoke()
            data = null
            time = 0L
        }
    }

    /**
     * 检查并且获取数据,但是需要自己加协程锁
     */
    private suspend fun checkAndGetDataWithNoMutex(newDataLambda: suspend () -> T): T {
        val oldData = data
        return if (oldData == null || _currentTimeMillis() - time >= cacheTime * 60000) {
            val newData = newDataLambda()
            data = newData
            time = _currentTimeMillis()
            newData
        } else {
            oldData
        }
    }
}