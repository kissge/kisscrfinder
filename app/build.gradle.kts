import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

// Release signing: environment variables (CI) or keystore.properties (local, gitignored).
// Set up with scripts/setup-signing.sh.
val keystoreProperties =
    Properties().apply {
        rootProject
            .file("keystore.properties")
            .takeIf { it.exists() }
            ?.inputStream()
            ?.use(::load)
    }

fun signingValue(
    property: String,
    env: String,
): String? = providers.environmentVariable(env).orNull ?: keystoreProperties.getProperty(property)

android {
    namespace = "do_.eki.yo.kisscrfinder"
    compileSdk {
        version =
            release(37) {
                minorApiLevel = 2
            }
    }

    defaultConfig {
        applicationId = "do_.eki.yo.kisscrfinder"
        minSdk = 26
        targetSdk = 37
        // CI passes the run number so every build can be installed as an update.
        versionCode = providers.environmentVariable("VERSION_CODE").orNull?.toInt() ?: 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            signingValue("storeFile", "SIGNING_KEYSTORE_PATH")?.let { storeFile = file(it) }
            storePassword = signingValue("storePassword", "SIGNING_PASSWORD")
            keyAlias = signingValue("keyAlias", "SIGNING_KEY_ALIAS") ?: "kisscrfinder"
            keyPassword = signingValue("keyPassword", "SIGNING_PASSWORD")
        }
    }

    buildTypes {
        debug {
            // Lets a local debug build sit next to the CI-built release on one device.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
