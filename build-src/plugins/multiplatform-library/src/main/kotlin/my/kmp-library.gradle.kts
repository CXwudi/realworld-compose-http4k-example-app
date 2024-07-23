package my

import com.goncalossilva.useanybrowser.useAnyBrowser
import my.util.Libs
import my.util.Versions

/**
 * Common Kotlin Multiplatform setup for both common and frontend modules.
 */
plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
  id("com.goncalossilva.useanybrowser")
}

kotlin {
  androidTarget()
  jvm()
  js(IR) {
    browser {
      testTask {
        useKarma {
          useAnyBrowser()
        }
      }
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "shared" // should we set same baseName for all gradle modules?
      isStatic = true
    }
  }

  applyDefaultHierarchyTemplate()

  sourceSets {
    commonMain.dependencies {
      implementation(kotlin("stdlib"))
      implementation(Libs.SerializationJson)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
    }

    val commonJvmMain by creating {
      dependsOn(commonMain.get())
    }

    jvmMain.get().dependsOn(commonJvmMain)
    androidMain.get().dependsOn(commonJvmMain)
  }
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(Versions.Java)
  }
}

android {
  compileSdk = 34
  defaultConfig {
    minSdk = 26
  }
  // jvm version is covered by java toolchain above
}
