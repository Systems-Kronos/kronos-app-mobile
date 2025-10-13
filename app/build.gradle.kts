plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.kronosprojeto"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kronosprojeto"
        minSdk = 33
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Retrofit e conversores
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

    // OkHttp e Logging Interceptor (necessário para HttpLoggingInterceptor)
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Outras libs
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1") {
        exclude(group = "com.android.support", module = "support-compat")
    }
    implementation("com.jakewharton.threetenabp:threetenabp:1.4.4")
    implementation("androidx.palette:palette:1.0.0")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation ("com.google.android.material:material:1.10.0")

    // Navigation Component (para BottomNavigationView + Fragments)
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.1")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.1")
}
