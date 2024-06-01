buildscript {

    repositories {
        // Make sure that you have the following two repositories
        google()  // Google's Maven repository
        mavenCentral()  // Maven Central repository
    }

    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
}