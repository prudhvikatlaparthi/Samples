plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.compose")
    kotlin("plugin.serialization") version "1.8.21"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
                implementation(libs.runtime)
                implementation(libs.foundation)
                implementation(libs.material)
                implementation(libs.components.resources)
                implementation(libs.kamel.image)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                api(libs.mvvm.core) // only ViewModel, EventsDispatcher, Dispatchers.UI
                api(libs.mvvm.compose) // api mvvm-core, getViewModel for Compose Multiplatfrom
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.pru.birdapp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}
