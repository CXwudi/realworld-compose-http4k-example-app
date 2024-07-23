plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.compose)
  alias(libs.plugins.kotlinCompose)
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

      implementation(libs.dev.androidx.appcompat)
      implementation(libs.dev.androidx.coreKtx)
      implementation(libs.dev.androidx.activityCompose)

      implementation(libs.dev.decompose)
      implementation(libs.dev.decomposeCompose)
      implementation(libs.dev.mvikotlin)
      implementation(libs.dev.mvikotlinMain)
      implementation(libs.dev.mvikotlinCoroutines)
      implementation(libs.dev.mvikotlinLogging)

      implementation(libs.dev.kmlogging)

      implementation(project.dependencies.platform(libs.dev.koinBom))
      implementation("io.insert-koin:koin-core")
      implementation("io.insert-koin:koin-android")

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
  compileSdk = 34
  defaultConfig {
    applicationId = "mikufan.cx.conduit.frontend.app.android"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }
}