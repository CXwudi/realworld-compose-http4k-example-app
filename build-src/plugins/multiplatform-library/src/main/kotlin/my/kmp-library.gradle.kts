package my

import my.util.Libs
import my.util.Versions
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin

/**
 * Common Kotlin Multiplatform setup for both common and frontend modules.
 */
plugins {
  kotlin("multiplatform")
  id("com.android.library")
  kotlin("plugin.serialization")
}

kotlin {
  androidTarget()
  jvm()
  js(IR) {
    browser {
      testTask {
        useKarma {
          // for developers, please use your own browsers in convenience
          useChromiumHeadless()
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
  testOptions {
    targetSdk = 34
  }
  lint {
    targetSdk = 34
  }
  // jvm version is covered by java toolchain above
}

// convenient way to automatically update yarn.lock if dep changes
plugins.withType<YarnPlugin> {
  the<org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension>().apply {
    yarnLockAutoReplace = true
  }
}
