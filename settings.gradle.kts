pluginManagement {
    repositories {
        // 阿里云 Gradle 插件仓库镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        // 阿里云 Google 镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        // 阿里云 Maven 中央仓库镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        // 备用：原始仓库（如果镜像不可用）
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 阿里云 Google 镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/google")
        }
        // 阿里云 Maven 中央仓库镜像
        maven {
            url = uri("https://maven.aliyun.com/repository/public")
        }
        // 备用：原始仓库（如果镜像不可用）
        google()
        mavenCentral()
    }
}

rootProject.name = "Comment"
include(":app")
 