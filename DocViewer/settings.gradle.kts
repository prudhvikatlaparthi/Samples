pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
//        maven { url = uri("https://mvnrepository.com/artifact/com.aspose/aspose-words") }
        maven {
            url = uri("https://releases.aspose.com/java/repo/")
            name = "Aspose Java API"
            content {
                includeGroup("com.aspose")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
//        maven { url = uri("https://mvnrepository.com/artifact/com.aspose/aspose-words") }
        maven {
            url = uri("https://releases.aspose.com/java/repo/")
            name = "Aspose Java API"
            content {
                includeGroup("com.aspose")
            }
        }
    }
}

rootProject.name = "DocViewer"
include(":app")
include(":flutter")

project(":flutter").projectDir = File(rootDir, "file_view")