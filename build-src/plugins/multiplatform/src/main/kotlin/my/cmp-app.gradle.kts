package my

import my.util.Libs

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  kotlin("plugin.compose")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      // coroutine
      implementation(project.dependencies.platform(Libs.CoroutinesBom))
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
      // compose
      implementation(compose.runtime)
      implementation(compose.ui)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.components.resources)
      // decompose + mvikotlin
      implementation(Libs.Decompose)
      implementation(Libs.DecomposeCompose)
      implementation(Libs.KotlinLogging)
      // koin
      implementation(project.dependencies.platform(Libs.KoinBom))
      implementation("io.insert-koin:koin-core")
    }
  }
}