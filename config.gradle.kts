//ext {
////    maven { url = uri("https://maven.aliyun.com/repository/public/")}
////    maven { url = uri("https://maven.aliyun.com/repository/google/")}
////    maven { url = uri("https://maven.aliyun.com/repository/jcenter/")}
////    maven { url = uri("https://maven.aliyun.com/repository/central/")}
////
////    maven { url 'https://maven.aliyun.com/nexus/content/groups/public/' }
////    maven { url 'https://maven.aliyun.com/nexus/content/repositories/jcenter' }
////    maven { url 'https://maven.aliyun.com/nexus/content/repositories/google' }
////    maven { url 'https://maven.aliyun.com/nexus/content/repositories/gradle-plugin' }
//    // App dependencies
//    supportLibraryVersion = '28.0.0'
//    // kotlin version
//    kotlin_version = '1.9.0'
//    extra["kotlin_version"] = kotlin_version
//    val
//    compose_version = '1.4.3'
//    hilt_version = '2.44'
//    cfgs = [
//            compileSdkVersion        : 33,
//            minSdkVersion            : 24,
//            targetSdkVersion         : 27,
//            versionCode              : 1,
//            versionName              : "1.0'",
//            testInstrumentationRunner: "androidx.test.runner.AndroidJUnitRunner",
//            consumerProguardFiles    : 'consumer-rules.pro',
//            jvmTarget               : '11',
//            javaVersion              : JavaVersion.VERSION_11,
//    ]
//
//    composeLib = [
//            //compose
//            compose_ui                  : "androidx.compose.ui:ui",
//            compose_graphics            : "androidx.compose.ui:ui-graphics",
//            compose_material            : "androidx.compose.material3:material3",
//            compose_ui_preview          : "androidx.compose.ui:ui-tooling-preview",
//            compose_activity            : "androidx.activity:activity-compose:$compose_version",
//            compose_constraintlayout    : "androidx.constraintlayout:constraintlayout-compose:1.0.0",
//            //compose test
//            test_coreKtx                : "androidx.test:core-ktx:1.4.0",
//            test_compose_ui             : "androidx.compose.ui:ui-test-junit4",
//            //debugImplementation
//            test_compose_tooling        : "androidx.compose.ui:ui-tooling",
//            test_compose_manifest       : "androidx.compose.ui:ui-test-manifest",
//            compose_bom                 : "androidx.compose:compose-bom:2023.03.00",
//
//
//    ]
//
//    hintLib = [
//        //hilt 需要再app build.gradle 的plugins中增加 id 'com.google.dagger.hilt.android' version '2.45' apply false
//        "hilt"                      : "com.google.dagger:hilt-android:$hilt_version",
//        "hilt_kapt"                 : "com.google.dagger:hilt-android-compiler:$hilt_version",
//        "hilt_navigation"           : "androidx.hilt:hilt-navigation-compose:1.0.0"
//    ]
//
//    testLib = [
//            //testImplementation
//            junit                       : 'junit:junit:4.13.2',
//            //androidTestImplementation
//            extJunit                    : 'androidx.test.ext:junit:1.1.5',
//            espressoCore                : 'androidx.test.espresso:espresso-core:3.5.1',
//    ]
//
//    des = [
//            //base implementation
//            kotlin_stdlib               : "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version",
//            coreKtx                     : "androidx.core:core-ktx:$kotlin_version",
//            lifecycle_runtime           : 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1',
//            appcompat                   : 'androidx.appcompat:appcompat:1.6.1',
//            material                    : 'com.google.android.material:material:1.8.0',
//
//
//            // DataStore
//            datastore                   : "androidx.datastore:datastore-preferences:1.0.0",
//    ]
//
//    sign = [
//            file         : '../arielrelease.keystore',
//            keyAlias     : 'ariel',
//            storePassword: '123456',
//            keyPassword  : '123456'
//    ]
//}