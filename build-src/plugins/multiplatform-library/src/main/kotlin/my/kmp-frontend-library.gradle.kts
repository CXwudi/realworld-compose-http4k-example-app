package my

import dev.mokkery.MockMode
import my.util.Libs

/**
 * Common Kotlin Multiplatform setup for frontend modules,
 * built on top of [my.kmp-library] with frontend specific common dependencies.
 */
plugins {
  id("my.kmp-library")
  id("dev.mokkery")
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      implementation(project.dependencies.platform(Libs.CoroutinesBom))
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
      implementation(Libs.EssentyLifecycleCoroutines)
      implementation(Libs.Decompose)
      implementation(Libs.DecomposeCompose)
      implementation(Libs.MviKotlin)
      implementation(Libs.MviKotlinMain)
      implementation(Libs.MviKotlinCoroutines)
      implementation(Libs.MviKotlinLogging)
      implementation(Libs.KmLogging)
      implementation(project.dependencies.platform(Libs.KoinBom))
      implementation("io.insert-koin:koin-core")
    }

    commonTest.dependencies {
      implementation("io.insert-koin:koin-test")
    }

    androidMain.dependencies {
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
      implementation(Libs.AndroidXCoreKtx)
      implementation(Libs.AndroidXLifecycleKtx)
      implementation(Libs.AndroidXAppCompat)
    }

    jvmMain.dependencies {
      implementation(Libs.Logback)
      implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
    }
  }
}

mokkery {
  defaultMockMode = MockMode.autoUnit
}