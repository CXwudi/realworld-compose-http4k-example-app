plugins {
  id("my.cmp-app")
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

      implementation(libs.dev.kmlogging)

      implementation(project.dependencies.platform(libs.dev.kotlinWrapper))
      implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
      implementation("co.touchlab:stately-common:2.1.0")
    }
  }
}