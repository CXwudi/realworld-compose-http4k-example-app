pluginManagement {
  repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
    gradlePluginPortal()
  }
  includeBuild("../build-src/settings")
  includeBuild("../build-src/plugins")
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
  versionCatalogs {
    create("libs") {
      from(files("../build-src/libs.versions.toml"))
    }
  }
}

rootProject.name = "conduit-frontend"

plugins {
  // my setting plugin that simply has some other setting plugins where versions are managed in version catalogs
  id("my.root-settings-plugin")
}

includeBuild("../conduit-common")

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
    termsOfUseAgree = "yes"
    publishing.onlyIf {
      !System.getenv("CI").isNullOrEmpty()
    }
  }

}

fileTree(".").matching {
  include("*/build.gradle.kts")
  include("*/build.gradle")
}.files.map { it.parentFile.name }.forEach { include(it) }

