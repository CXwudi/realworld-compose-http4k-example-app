plugins {
  id("my.kmp-frontend-library")
}

android {
  namespace = "mikufan.cx.conduit.frontend.logic"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      // add dependencies that are specific for this decompose logic module
      // dependencies used in both this module and compose ui module are extracted into the precompiled script plugin
      implementation(libs.dev.kstore)
      // workaround for JS from https://github.com/cashapp/sqldelight/issues/4357#issuecomment-1839905700
      // dear to manage it in version catalog
      implementation("co.touchlab:stately-common:2.0.7")
    }

    // and platform specific dependencies only used in this module

    commonJvmMain.dependencies {
      implementation(libs.dev.kstore.file)
    }

    androidMain.dependencies {
    }

    jvmMain.dependencies {
    }

    jsMain.dependencies {
      implementation(libs.dev.kstore.storage)
    }

    iosMain.dependencies {
      implementation(libs.dev.kstore.file)
    }
  }
}