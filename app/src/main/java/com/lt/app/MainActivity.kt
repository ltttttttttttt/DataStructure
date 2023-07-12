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

package com.lt.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.lt.data_structure.basic_value.IntArrayList
import com.lt.data_structure.time.DownTimeList
import com.lt.data_structure.time.DownTimeSuspendList
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val scope = MainScope()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv = findViewById<TextView>(R.id.tv)
        tv.text = IntArrayList(intArrayOf(1, 2, 3, 4, 5)).toString()

        val list = DownTimeSuspendList<Int>(3000, scope)
        //val list = DownTimeList<Int>(3000, scope)
        scope.launch {
            delay(3000)
            list.add(1)
            list.add(2)
            list.add(3)
            delay(1000)
            list.add(4)
            list.add(5)
            list.add(6)
            delay(2000)
            list.removeFirst(6)
            list.add(7)
            list.add(8)
            list.add(9)
        }
        scope.launch {
            repeat(100) {
                delay(100)
                tv.text = list.toList().joinToString { it.toString() } + list.contains(6)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}