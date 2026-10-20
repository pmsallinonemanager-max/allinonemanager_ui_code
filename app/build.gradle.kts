import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Per-machine overrides go in local.properties (not committed), e.g.
//   aiom.baseUrl.local=http://192.168.1.5:8087/aiom/
val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) file.inputStream().use { load(it) }
}

fun baseUrlFor(env: String, default: String): String =
    "\"" + localProps.getProperty("aiom.baseUrl.$env", default) + "\""

android {
    namespace = "com.allinonemanager"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.allinonemanager"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Pick the environment from Build Variants (e.g. localDebug, devDebug, prodRelease)
    flavorDimensions += "env"
    productFlavors {
        create("local") {
            dimension = "env"
            applicationIdSuffix = ".local"
            // 10.0.2.2 is the host machine's localhost as seen from the Android emulator
            buildConfigField("String", "BASE_URL", baseUrlFor("local", "http://10.0.2.2:8087/aiom/"))
        }
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "BASE_URL", baseUrlFor("dev", "https://dev.example.com/aiom/"))
        }
        create("prod") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", baseUrlFor("prod", "https://api.example.com/aiom/"))
        }
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
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)  // add this
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
}