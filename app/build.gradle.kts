plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.jetbrainsKotlinAndroid)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.daggerHiltKsp)
  alias(libs.plugins.daggerHilt)
}

android {
  namespace = "com.shermanrex.recorderApp"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.shermanrex.recorderApp"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      merges += "META-INF/LICENSE.md"
      merges += "META-INF/LICENSE-notice.md"
    }
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)

  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.lifecycle.service)

  debugImplementation(libs.test.core)

  ksp(libs.androidx.hilt.compiler)

  testImplementation(libs.junit.jupiter)
  testImplementation(libs.junit.junit)
  testImplementation(libs.junit.junit)
  testImplementation(libs.google.truth)
  testImplementation(libs.mockito.android)
  testImplementation(libs.mockito.core)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.android.mockk)

  androidTestImplementation(libs.google.truth)
  androidTestImplementation(libs.android.mockk)
  androidTestImplementation(libs.mockito.android)
  androidTestImplementation(libs.mockito.core)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.ui.test.runner)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)

  implementation(libs.androidx.documentfile)

  implementation(libs.androidx.lifecycle.viewmodel.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.lifecycle.runtime.compose)

  implementation(libs.androidx.datastore.preferences)

  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  implementation(libs.androidx.constraintlayout.compose)
  implementation(libs.androidx.runtime)

  implementation(libs.androidx.media3.exoplayer)
  implementation(libs.androidx.media3.session)
  implementation(libs.androidx.media3.extractor)

  implementation(libs.androidx.hilt.navigation.compose)

}