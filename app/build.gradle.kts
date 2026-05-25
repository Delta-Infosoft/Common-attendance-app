plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("kotlin-parcelize")
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.i.common.attendance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.i.common.attendance"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        viewBinding = true
        buildConfig = true
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
    flavorDimensions += "app"

    productFlavors {
        create("waterman") {
            dimension = "app"
            applicationId = "com.i.unatti.attendanceapp"
            resValue("string", "app_name", "C Wman Att")
            buildConfigField("String", "BASE_PATH", "\"DeltaAttendanceAPIWIPL/\"")
        }
        create("unnati") {
            dimension = "app"
            applicationId = "com.i.waterman.attendanceapp"
            resValue("string", "app_name", "C Unnati Att")
            buildConfigField("String", "BASE_PATH", "\"Unnati/DeltaAttendanceAPI/\"")
        }
        create("duke") {
            dimension = "app"
            applicationId = "com.i.waterman.attendanceapp"
            resValue("string", "app_name", "C Duke Att")
            buildConfigField("String", "BASE_PATH", "\"\"")
        }
        create("flotech") {
            dimension = "app"
            applicationId = "com.i.waterman.attendanceapp"
            resValue("string", "app_name", "C Flotech Att")
            buildConfigField("String", "BASE_PATH", "\"Flotech/DeltaAttendanceAPI/\"")
        }
        create("singla") {
            dimension = "app"
            applicationId = "com.i.singla.iattendanceapp"
            resValue("string", "app_name", "Singla iAttendance")
            buildConfigField("String", "BASE_PATH", "\"DeltaAttendanceAPI/\"")
        }
        create("algo") {
            dimension = "app"
            applicationId = "com.i.algo.iattendanceapp"
            resValue("string", "app_name", "Algo iAttendance")
            buildConfigField("String", "BASE_PATH", "\"ALGO/DeltaAttendanceAPI/\"")
        }
        create("mascot") {
            dimension = "app"
            applicationId = "com.i.unatti.attendanceapp"
            resValue("string", "app_name", "Mascot iAttendance")
            buildConfigField("String", "BASE_PATH", "\"Mascot/DeltaAttendanceAPI/\"")
        }
    }

    ndkVersion = "27.0.12077973"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.activity:activity-ktx:1.8.2")

    // For hilt Implementation
    implementation ("com.google.dagger:hilt-android:2.57.2")
    kapt ("com.google.dagger:hilt-android-compiler:2.57.2")
    implementation ("androidx.hilt:hilt-work:1.1.0")
    kapt ("androidx.hilt:hilt-compiler:1.1.0")

    //Indicator Dots
    implementation("com.tbuonomo:dotsindicator:5.1.0")

    //This for the Calender View
    implementation("com.sickmartian.calendarview:calendarview:1.0.0") {
        exclude(group = "com.android.support", module = "support-v4")
    }
    //Security Crypto for Encryption Decryption
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    //Room DB
    implementation ("androidx.room:room-runtime:2.8.4")
    kapt ("androidx.room:room-compiler:2.8.4")
    implementation ("androidx.room:room-ktx:2.8.4")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // GSON
    implementation("com.google.code.gson:gson:2.9.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0")) // Use the latest version
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Use the kapt plugin for annotation processing
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    /*pdf*/
    implementation("com.itextpdf:itextg:5.5.10")

    implementation("androidx.camera:camera-core:1.6.1")
    implementation("androidx.camera:camera-camera2:1.6.1")
    implementation("androidx.camera:camera-lifecycle:1.6.1")
    implementation("androidx.camera:camera-view:1.6.1")
}