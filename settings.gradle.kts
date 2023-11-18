pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/google") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }

        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/google") }
        maven { url = uri("https://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }

//        maven { url = uri("https://maven.aliyun.com/repository/public/")}
//        maven { url = uri("https://maven.aliyun.com/repository/google/")}
//        maven { url = uri("https://maven.aliyun.com/repository/jcenter/")}
//        maven { url = uri("https://maven.aliyun.com/repository/central/")}
        google()
        mavenCentral()
    }
//    versionCatalogs {
//        create("libs") {
//            from(files("./gradle/libs.versions.toml"))
//        }
//    }
}

rootProject.name = "RemoteControl"
include(":app")
include(":base")
include(":remoteChannel")
