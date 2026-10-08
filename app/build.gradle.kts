plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.korenabdush.simplecrm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.korenabdush.simplecrm"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        val pendoKey = providers.gradleProperty("pendo.apiKey").getOrElse("YOUR_PENDO_APP_KEY")
        val pendoScheme = providers.gradleProperty("pendo.scheme").getOrElse("pendo-scheme")
        buildConfigField("String", "PENDO_API_KEY", "\"$pendoKey\"")
        manifestPlaceholders["pendoScheme"] = pendoScheme
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("sdk.pendo.io:pendoIO:3.13.+") { isChanging = true }

}
