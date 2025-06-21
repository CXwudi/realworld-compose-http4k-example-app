/**
 * The pure UI module in Compose Multiplatform.
 *
 * All navigation, state, routing, etc. should go to the [frontend-decompose-logic] module.
 */
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
      implementation(compose.material3AdaptiveNavigationSuite)
      implementation(libs.dev.frontend.compose.adaptive)
      implementation(libs.dev.frontend.compose.adaptiveLayout)
      implementation(libs.dev.frontend.compose.adaptiveNavigation)
      implementation(libs.dev.frontend.compose.materialIconsCore)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      implementation(libs.dev.frontend.decomposeCompose)
      implementation(libs.dev.frontend.decomposeComposeExperimental)
      implementation("io.insert-koin:koin-compose")

      implementation(libs.dev.frontend.coil.compose)
      implementation(libs.dev.frontend.coil.ktor3)
      implementation(libs.dev.frontend.coil.cacheControl)
      implementation(libs.dev.frontend.coil.svg)

      implementation(libs.dev.datetime)

    }
    commonJvmMain.dependencies {
      implementation(compose.uiTooling)
      implementation(compose.preview)
    }
    jvmMain.dependencies {
      implementation(compose.desktop.common)
    }
    androidMain.dependencies {
      implementation(libs.dev.frontend.androidx.activityCompose)
      implementation("io.insert-koin:koin-androidx-compose")
    }
  }
}

compose.resources {
  packageOfResClass = "mikufan.cx.conduit.frontend.ui.resources"
}

composeCompiler {
  // string skipping mode is now enabled by default since Kotlin 2.0.20
  stabilityConfigurationFiles.addAll(layout.projectDirectory.file("compose-stability.txt"))
  reportsDestination = layout.buildDirectory.dir("compose-reports")
  metricsDestination = layout.buildDirectory.dir("compose-reports")
}
