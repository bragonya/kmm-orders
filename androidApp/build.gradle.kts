plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.orders.android"
    compileSdk = 32
    defaultConfig {
        applicationId = "com.example.orders.android"
        minSdk = 21
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    val ktorVersion = "2.1.2"
    val koinVersion = "3.2.0"
    implementation(project(":shared"))
    implementation("androidx.compose.ui:ui:1.2.1")
    implementation("androidx.compose.ui:ui-tooling:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.2.1")
    implementation("androidx.compose.material:material:1.2.1")
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.insert-koin:koin-core:${koinVersion}")
    implementation("io.insert-koin:koin-test:${koinVersion}")
    implementation("io.insert-koin:koin-android:${koinVersion}")
}
