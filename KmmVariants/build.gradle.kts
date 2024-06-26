buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("com.android.tools.build:gradle:7.0.3")
    }
        val kk = "1212"
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