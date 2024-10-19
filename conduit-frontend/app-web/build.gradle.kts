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

      implementation(project.dependencies.platform(libs.dev.frontend.kotlinWrapper))
      implementation("org.jetbrains.kotlin-wrappers:kotlin-browser")
    }
  }
}