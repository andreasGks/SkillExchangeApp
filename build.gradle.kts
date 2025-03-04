// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }

    dependencies {
        // ✅ Updated to match the latest Firebase and Google services versions
        classpath("com.google.gms:google-services:4.4.2")

        // ✅ Ensuring compatibility with Gradle 8.2.2
        classpath("com.android.tools.build:gradle:8.2.2")

        // ✅ Matching Kotlin plugin version (1.9.x is recommended, but kept as 1.8.22 for now)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }
}

plugins {
    // ✅ Ensuring the correct AGP version for compatibility
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false

}
