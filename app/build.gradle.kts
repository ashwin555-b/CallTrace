
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.calltrace"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calltrace"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.55")
    implementation("com.googlecode.libphonenumber:geocoder:2.230")
}
