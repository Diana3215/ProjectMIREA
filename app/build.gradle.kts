plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services") version "4.4.1" apply false
}

android {
    namespace = "com.example.projectmirea"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projectmirea"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.play.services.tasks)
    implementation(libs.firebase.firestore)
    // Firebase BoM (Bill of Materials)
    platform("com.google.firebase:firebase-bom:32.0.0")

    // Firebase Firestore dependency
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")


    // Additional dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}