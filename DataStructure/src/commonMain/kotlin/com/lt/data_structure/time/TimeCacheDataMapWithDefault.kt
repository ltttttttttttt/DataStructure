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

/**
 * creator: lt  2022/2/25  lt.dygzs@qq.com
 * effect : 在服务器中有时效性的多个键值对数据内存缓存
 * warning: 共用一个mutex
 */
class TimeCacheDataMapWithDefault<K : Any, V>(
    cacheTime: Int = 60,//缓存的时效,单位分钟
    private val defaultNewDataLambda: suspend (key: K) -> V,//默认的获取新数据的lambda(调用[getData]方法是无需再传lambda)
) : TimeCacheDataMap<K, V>(cacheTime) {
    suspend fun getData(key: K) = getData(key, defaultNewDataLambda)

    suspend fun <U> getDataWithMutex(
        key: K,
        gotBlockWithMutex: suspend (key: K, V) -> U
    ) = getDataWithMutex(key, defaultNewDataLambda, gotBlockWithMutex)
}