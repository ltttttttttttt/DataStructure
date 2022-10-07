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

package com.lt;

import com.lt.data_structure.basic_value.IntArrayList;

/**
 * creator: lt  2022/10/7  lt.dygzs@qq.com
 * effect :
 * warning:
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        IntArrayList intArrayList = new IntArrayList();
        intArrayList.add(6);
        intArrayList.add(6);
        intArrayList.add(6);
        System.out.println(intArrayList);
    }
}