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
      implementation(Libs.EssentyLifecycleCoroutines)
      implementation(Libs.Decompose)
      implementation(Libs.DecomposeCompose)
      implementation(Libs.MviKotlin)
      implementation(Libs.MviKotlinMain)
      implementation(Libs.MviKotlinCoroutines)
      implementation(Libs.MviKotlinLogging)
      implementation(Libs.KotlinLogging)
      // koin
      implementation(project.dependencies.platform(Libs.KoinBom))
      implementation("io.insert-koin:koin-core")
    }
  }
}