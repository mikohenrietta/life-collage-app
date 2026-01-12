plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("io.realm.kotlin")
}

android {
    namespace = "com.example.lifecollage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lifecollage"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation("io.realm.kotlin:library-base:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.0")
    // Retrofit: Used for REST API calls (GET, POST, etc.)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // GSON Converter: Converts JSON from server into your Kotlin Objects automatically
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp: The engine that powers Retrofit and handles WebSockets
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Logging Interceptor: Lets you see the JSON sent/received in Logcat (Crucial for debugging)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}