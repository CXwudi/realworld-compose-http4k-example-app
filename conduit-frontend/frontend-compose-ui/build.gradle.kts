plugins {
  id("my.kmp-frontend-library")
  alias(libs.plugins.kotlinCompose)
  alias(libs.plugins.compose)
}

android {
  namespace = "mikufan.cx.conduit.frontend.ui"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(project(":frontend-decompose-logic"))

      implementation(compose.runtime)
      implementation(compose.ui)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.dev.windowSizeClassMultiplatform)

      implementation("io.insert-koin:koin-compose")
    }
    commonJvmMain.dependencies {
      implementation(compose.uiTooling)
      implementation(compose.preview)
    }
    jvmMain.dependencies {
      implementation(compose.desktop.common)
    }
    androidMain.dependencies {
      implementation(libs.dev.androidx.activityCompose)
      implementation("io.insert-koin:koin-androidx-compose")
    }
  }
}

compose.resources {
  packageOfResClass = "mikufan.cx.conduit.frontend.ui.resources"
}

composeCompiler {
  // string skipping mode is now enabled by default since Kotlin 2.0.20
  stabilityConfigurationFile = projectDir.resolve("compose-stability.txt")
  reportsDestination = layout.buildDirectory.dir("compose-reports")
  metricsDestination = layout.buildDirectory.dir("compose-reports")
}
