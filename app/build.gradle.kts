plugins {
    alias(notation = libs.plugins.android.application)
    alias(notation = libs.plugins.jetbrains.kotlin.android)
    alias(notation = libs.plugins.ksp)
    alias(notation = libs.plugins.hilt.android)
    alias(notation = libs.plugins.kotlinx.serialization)
    alias(notation = libs.plugins.compose.compiler)
    alias(notation = libs.plugins.google.services)
}

android {
    namespace = "com.ddd.attendance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ddd.attendance"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.zxing.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(dependencyNotation = libs.androidx.navigation.compose)
    implementation(dependencyNotation = libs.hilt.android)
    implementation(dependencyNotation = libs.hilt.navigation.compose)
    ksp(dependencyNotation = libs.hilt.compiler)
    implementation(dependencyNotation = libs.retrofit2)
    implementation(dependencyNotation = libs.retrofit2.converter.gson)
    implementation(dependencyNotation = libs.retrofit2.converter.scalars)
    implementation(dependencyNotation = libs.okhttp3)
    implementation(dependencyNotation = libs.okhttp3.logging)
    implementation(dependencyNotation = libs.gson)
    implementation(dependencyNotation = libs.converter.serialization)
    implementation(dependencyNotation = libs.kotlin.serialization)
    implementation(dependencyNotation = libs.androidx.constraintlayout.compose)
    implementation(dependencyNotation = libs.androidx.datastore)
    implementation(dependencyNotation = platform(libs.firebase.bom))
    implementation(dependencyNotation = libs.firebase.auth.ktx)
    implementation(dependencyNotation = libs.play.services.auth)

    implementation(dependencyNotation = libs.glide.core)
    implementation(dependencyNotation = libs.lottie.compose)

    debugImplementation(dependencyNotation = libs.chucker.debug)
    releaseImplementation(dependencyNotation = libs.chucker.release)
}