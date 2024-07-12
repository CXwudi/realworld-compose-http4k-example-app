plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.kotlinCompose)
}

kotlin {
  js(IR) {
    browser()
    binaries.executable()
  }

  sourceSets {
    jsMain.dependencies {
      implementation(project(":frontend-decompose-logic"))
      implementation(project(":frontend-compose-ui"))
      implementation(compose.ui)
      implementation(compose.foundation)

      implementation(libs.dev.decompose)
      implementation(libs.dev.decomposeCompose)
      implementation(libs.dev.mvikotlin)
      implementation(libs.dev.mvikotlinMain)
      implementation(libs.dev.mvikotlinCoroutines)
      implementation(libs.dev.mvikotlinLogging)

      implementation(libs.dev.kmlogging)
      // workaround for JS from https://github.com/cashapp/sqldelight/issues/4357#issuecomment-1839905700
      // dear to manage it in version catalog
      implementation("co.touchlab:stately-common:2.0.7")

      implementation(project.dependencies.platform(libs.dev.koinBom))
      implementation("io.insert-koin:koin-core")

      implementation(project.dependencies.platform(libs.dev.kotlinWrapper))
      implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
    }
  }
}