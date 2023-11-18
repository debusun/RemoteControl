// Top-level build file where you can add configuration options common to all sub-projects/modules.

//buildscript {
//    repositories {
//        maven { url = uri("https://maven.aliyun.com/repository/public/")}
//        maven { url = uri("https://maven.aliyun.com/repository/google/")}
//        maven { url = uri("https://maven.aliyun.com/repository/jcenter/")}
//        maven { url = uri("https://maven.aliyun.com/repository/central/")}
//        google()
//        mavenCentral()

        // Android Build Server
//        maven { url = uri("../nowinandroid-prebuilts/m2repository") }
//    }
//    dependencies {
//        classpath(libs.google.oss.licenses.plugin) {
//            exclude(group = "com.google.protobuf")
//        }
//    }
//}

plugins {
//    alias(libs.plugins.android.application) apply false
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.android.library") version "8.1.2" apply false
}