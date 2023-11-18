@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.fastdds.remotechannel"
    compileSdk = 33

    defaultConfig {
        minSdk = 25

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
                // 添加自定义 CMake 命令
                arguments("-DANDROID_ABI=armeabi-v7a",
                    "-DANDROID_NDK=/home/bcy/Android/Sdk/ndk/25.1.8937393",
                    "-DANDROID_NATIVE_API_LEVEL=25",
                    "-D__ANDROID_API__=25",
                    "-DANDROID_PLATFORM=android-28",
                    "-DBUILD_SHARED_LIBS=true",
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DTHIRDPARTY=ON")
            }
        }
        ndk {
            // 设置支持的SO库架构
            abiFilters.add("armeabi-v7a")//, 'x86', 'armeabi-v7a', 'x86_64', 'arm64-v8a'
        }
    }
    sourceSets["main"].jniLibs.srcDir("libs")
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    testImplementation(libs.junit4)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}