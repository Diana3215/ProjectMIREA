plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services") version "4.4.1" apply true
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

    // Firebase Firestore dependency
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics:22.0.0")


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries


    // Additional dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}