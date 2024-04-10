plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.notespro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.notespro"
        minSdk = 28
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }

    packaging{
        resources.excludes.add("META-INF/NOTICE.md")
        resources.excludes.add("META-INF/LICENSE.md")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("pub.devrel:easypermissions:3.0.0")

    implementation ("com.makeramen:roundedimageview:2.3.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.sun.mail:android-mail:1.6.6")
    implementation ("com.sun.mail:android-activation:1.6.7")

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation("androidx.core:core-splashscreen:1.0.0")

    //in order for ksp to work make sure ksp version is same as your kotlin verion

    val room_version = "2.6.0"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    implementation ("androidx.room:room-ktx:$room_version")

    // Life Cycle Arch
    val lifecycleVersion = "2.6.2"
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    // Annotation processor
    ksp("androidx.lifecycle:lifecycle-compiler:$lifecycleVersion")
}