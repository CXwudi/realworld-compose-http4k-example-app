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
  enableStrongSkippingMode = true // not sure if it could break anything, for ref: https://developer.android.com/develop/ui/compose/performance/stability/strongskipping
  stabilityConfigurationFile = projectDir.resolve("compose-stability.txt")
  reportsDestination = layout.buildDirectory.dir("compose-reports")
  metricsDestination = layout.buildDirectory.dir("compose-reports")
}
