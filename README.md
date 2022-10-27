# DataStructure

Common user-defined data structures(常用的自定义的数据结构)

Applicable to jvm(适用于jvm语言,后续支持kotlin全平台)

## Add to your project(如何添加到你的项目中)

Step 1.Root dir, build.gradle.kts add:(第一步,在项目根目录,build.gradle.kts中添加)

```kotlin
buildscript {
    repositories {
        maven("https://jitpack.io")//this
        ...
    }
}

allprojects {
    repositories {
        maven("https://jitpack.io")//this
        ...
    }
}
```

Step 2.Your app dir, build.gradle.kts add:(第二步,在你app目录的build.gradle.kts中添加)

version
= [![](https://jitpack.io/v/ltttttttttttt/DataStructure.svg)](https://jitpack.io/#ltttttttttttt/DataStructure)

```kotlin
dependencies {
    ...
    implementation("com.github.ltttttttttttt:DataStructure:$version")//this, such as 1.0.4
}
```

## 可自动扩容的基础数据数组,参考ArrayList<T>,但使用不会自动拆装箱,提升性能

```kotlin
IntArrayList
LongArrayList
FloatArrayList
DoubleArrayList
BooleanArrayList
```

## LRU算法的map

```kotlin
LruMap
LruMapWithGetFirst
```

## 排序且去重的List

```kotlin
SortedAndNotRepeatList
```

## 会根据倒计时自动移除数据的map

```kotlin
DownTimeMap
```

## 在服务器中有时效性的数据内存缓存

```kotlin
TimeCacheData
TimeCacheDataMap
```

## kotlin的Pair数量不够用怎么办?用下面的

```kotlin
typealias Values2 = Pair
typealias Values3 = Triple
Values4
Values5
```
