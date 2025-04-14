plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.skillexchangeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skillexchangeapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
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
        viewBinding = true // Enables ViewBinding in the project
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.11.0")

    // Added SwipeRefreshLayout to avoid missing dependency issues
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Firebase dependencies
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx:22.0.0")
    implementation("com.google.firebase:firebase-database-ktx:20.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.8.1")
    implementation("com.google.firebase:firebase-appcheck-playintegrity:17.0.1")

    // Fragment KTX for easier fragment handling
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.13.2")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.media3.common.ktx)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.storage.ktx) // Ensure you have the correct version
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2") // For Glide's annotation processing

    // Circular image view for profile pics
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Navigation UI for `setupWithNavController`
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // PhotoView for zoomable images
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
}

configurations {
    all {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
    }
}