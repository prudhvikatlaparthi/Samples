buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.1.2")

        //Kotlin Serialization
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.10")

        //Nav Safe Args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.1")

        //Hilt Dagger
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")

        //Sql Delight
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.3")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}