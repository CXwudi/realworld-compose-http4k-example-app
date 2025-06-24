pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
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

rootProject.name = "conduit-common" // the name must be yarn compatible, hence using the directory name

plugins {
  // my setting plugin that simply has some other setting plugins where versions are managed in version catalogs
  id("my.root-settings-plugin")
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
    termsOfUseAgree = "yes"
    publishing.onlyIf {
      !System.getenv("CI").isNullOrEmpty()
    }
  }
}

