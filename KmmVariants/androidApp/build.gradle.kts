plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "com.pru.kmmvariants.android"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        /*val config : java.util.Properties? = rootProject.extensions as java.util.Properties
        config?.forEach { p ->
            buildConfigField("String",p.key,p.value)
            resValue("String",p.key,p.value)
        }*/
        /*rootProject.ext.config.each { p ->
            buildConfigField 'String', p.key, p.value
            resValue 'string', p.key, p.value
        }*/
    }
    flavorDimensions.add("dimension")
    productFlavors {
        create("dev") {
            dimension = "dimension"
        }
        create("qa") {
            dimension = "dimension"
        }
        create("uat") {
            dimension = "dimension"
        }
        create("prod") {
            dimension = "dimension"
        }
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
}