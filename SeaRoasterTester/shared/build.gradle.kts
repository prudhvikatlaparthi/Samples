plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("kotlin-parcelize")
    id("com.squareup.sqldelight")
}

version = "1.0"

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }

    val ktorVersion = "1.6.5"
    val serializationVersion = "1.3.0"
    val coroutinesVersion = "1.4.0"
    val sqlDelightVersion ="1.5.3"
    
    sourceSets {
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        //Shared
        val commonMain by getting{
            dependencies {
                //Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

                //Ktor Core
                implementation("io.ktor:ktor-client-core:$ktorVersion")

                //Kotlin Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")

                //Ktor logging
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation ("ch.qos.logback:logback-classic:1.2.3")

                //Setting Shared Pref
                implementation("com.russhwolf:multiplatform-settings-no-arg:0.8.1")

                //SqlDelight DB
                implementation("com.squareup.sqldelight:runtime:$sqlDelightVersion")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sqlDelightVersion")
            }
        }

        //Android
        val androidMain by getting {
            dependencies {
                //Ktor Android
                implementation("io.ktor:ktor-client-android:$ktorVersion")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")

                //SqlDelight Android
                implementation("com.squareup.sqldelight:android-driver:$sqlDelightVersion")
            }
        }

        //iOS
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                //Ktor iOs
                implementation("io.ktor:ktor-client-ios:$ktorVersion")

                //SqlDelight iOS
                implementation("com.squareup.sqldelight:native-driver:$sqlDelightVersion")
            }
        }
        val androidTest by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

sqldelight {
    database("AppDatabase") {
        packageName = "com.pru.searoastertester"
        sourceFolders = listOf("sqldelight")
    }
}