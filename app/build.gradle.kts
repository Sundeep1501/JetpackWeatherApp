plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.sundeep1501.weather"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.sundeep1501.weather"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Dagger Hilt Dependencies
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // For hiltViewModel() in Compose

    // Networking & Parsing Dependencies
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.play.services)

    // Image Loading
    implementation(libs.coil.compose)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Location
    implementation(libs.play.services.location)

    // Architecture & Screen Routing
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
