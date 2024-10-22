/**
 * The core frontend business logic and navigation module with multiplatform support,
 * powered by Decompose and MviKotlin.
 *
 * Must not contain any Compose Multiplatform related dependencies.
 * Failing to do so will break the WASM target unit tests.
 *
 * Thanks to Decompose, you can plug in any UI framework you want,
 * by simply importing this gradle module.
 */
plugins {
  id("my.kmp-frontend-library")
  alias(libs.plugins.ksp)
  alias(libs.plugins.ktorfit)
}

android {
  namespace = "mikufan.cx.conduit.frontend.logic"
}

kotlin {

  sourceSets {
    commonMain.dependencies {
      // add dependencies that are specific for this decompose logic module
      // dependencies used in both this module and compose ui module are extracted into the precompiled script plugin
      implementation("mikufan.cx.conduit:conduit-common")

      implementation(libs.dev.frontend.kstore)
      implementation(dependencies.platform(libs.dev.frontend.ktorBom))
      implementation("io.ktor:ktor-client-core")
      implementation("io.ktor:ktor-client-content-negotiation")
      implementation("io.ktor:ktor-serialization-kotlinx-json")
      implementation(libs.dev.frontend.ktorfitLibLight)
      // workaround for JS from https://github.com/cashapp/sqldelight/issues/4357#issuecomment-1839905700,
      // but for some reason I still need it even I removed sqldelight
      implementation("co.touchlab:stately-common:2.1.0")
    }

    // and platform specific dependencies only used in this module

    commonJvmMain.dependencies {
      implementation(libs.dev.frontend.kstore.file)
      implementation("io.ktor:ktor-client-okhttp")
    }

    commonWebMain.dependencies {
      implementation(libs.dev.frontend.kstore.storage)
      implementation("io.ktor:ktor-client-js")
    }

    androidMain.dependencies {
    }

    jvmMain.dependencies {
    }

    jsMain.dependencies {
    }

    wasmJsMain.dependencies {
    }

    iosMain.dependencies {
      implementation(libs.dev.frontend.kstore.file)
      implementation("io.ktor:ktor-client-darwin")
    }
  }
}

