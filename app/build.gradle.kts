import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.zenith"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.zenith"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        val apiKey = properties.getProperty("WEATHER_API_KEY")
            ?: throw GradleException("WEATHER_API_KEY not found in local.properties")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
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
    // 1. Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.play.services.location)
    implementation(libs.androidx.work.runtime.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.junit.ktx)

    // 2. Compose BOM (Bill of Materials) - EL MONYAFEESTO
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // 3. Compose UI & Material 3
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material:material-icons-extended")

    // 4. Lifecycle & Navigation
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-svg:2.6.0")
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("com.airbnb.android:lottie-compose:6.0.0")
    val room_version = "2.6.1"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // عشان نستخدم Coroutines و Flow
    ksp("androidx.room:room-compiler:$room_version")


    // 5. Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("junit:junit:4.13.2")
    // Mockito للتزييف (Mocking) في الـ Unit Tests العادية
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")

    // --- 2. اختبار الكوروتين والـ Flow (مهم جداً ليك) ---
    // لتشغيل runTest والتحكم في الوقت (Delay)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // Turbine: أسهل مكتبة لاختبار الـ Flow (بتخليك تعمل awaitItem)
    testImplementation("app.cash.turbine:turbine:1.0.0")

    // --- 3. اختبار الـ Architecture Components (ViewModel & LiveData) ---
    // عشان الـ InstantTaskExecutorRule اللي بيخلي الكود يشتغل متزامن
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // --- 4. اختبار الـ API (Retrofit) ---
    // MockWebServer بيعمل سيرفر وهمي يبعت JSON لـ Retrofit
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    // --- 5. اختبارات الأندرويد (Instrumented Tests - لـ Room و الـ Context) ---
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // عشان تقدر تشغل الكوروتين جوه الـ Instrumented Tests
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // مكتبة Turbine بردو بنحتاجها في الـ AndroidTest لو بنختبر الـ DAO اللي بيرجع Flow
    androidTestImplementation("app.cash.turbine:turbine:1.0.0")
    // مكتبة MockK الأساسية للـ Unit Tests
    testImplementation ("io.mockk:mockk:1.13.8")

    // لو حابب تستخدم Android MockK (في حالة إنك بتختبر حاجات بتعتمد على الـ Android Framework)
    androidTestImplementation ("io.mockk:mockk-android:1.13.8")
}