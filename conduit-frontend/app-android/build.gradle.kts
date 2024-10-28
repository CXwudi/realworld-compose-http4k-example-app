plugins {
  id("my.cmp-app")
  alias(libs.plugins.androidApplication)
}

kotlin {
  androidTarget()

  sourceSets {
    androidMain.dependencies {
      implementation(project(":frontend-decompose-logic"))
      implementation(project(":frontend-compose-ui"))

//      implementation(compose.runtime)
//      implementation(compose.ui)
//      implementation(compose.foundation)
//      implementation(compose.material3)
//      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(compose.uiTooling)
      implementation(compose.preview)

      implementation(libs.dev.frontend.androidx.appcompat)
      implementation(libs.dev.frontend.androidx.coreKtx)
      implementation(libs.dev.frontend.androidx.activityCompose)

      implementation("io.insert-koin:koin-android")
      implementation("io.insert-koin:koin-androidx-startup")

      implementation(libs.dev.frontend.slf4jAndroid)

    }
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
  }
}

android {
  namespace = "mikufan.cx.conduit.frontend.app.android"
  compileSdk = 35
  defaultConfig {
    applicationId = "mikufan.cx.conduit.frontend.app.android"
    minSdk = 26
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }
}