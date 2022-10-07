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
    implementation("com.github.ltttttttttttt:DataStructure:$version")//this, such as 1.0.0
}
```