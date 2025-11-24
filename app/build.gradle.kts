plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.resepmakanan"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.resepmakanan"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "AI_API_KEY",
            "\"${providers.gradleProperty("AI_API_KEY").orNull ?: ""}\""
        )
        buildConfigField(
            "String",
            "AI_BASE_URL",
            "\"${providers.gradleProperty("AI_BASE_URL").orNull ?: "https://api.cerebras.ai"}\""
        )
        buildConfigField(
            "String",
            "SP_API_KEY",
            "\"${providers.gradleProperty("SP_API_KEY").orNull ?: ""}\""
        )
        buildFeatures {
            buildConfig = true
        }
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Volley untuk API
    implementation("com.android.volley:volley:1.2.1")

    // glide untuk menampilkan gambar dari url
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // Material Design Components (untuk BottomNavigationView, CardView, dll.)
    implementation("com.google.android.material:material:1.12.0")

    // RecyclerView untuk daftar item
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // AI Chatbot
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

}