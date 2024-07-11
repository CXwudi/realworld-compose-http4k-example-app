plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.compose)
  alias(libs.plugins.kotlinCompose)
}

kotlin {
  jvm()
  sourceSets {
    jvmMain {
      dependencies {
        implementation(project(":frontend-decompose-logic"))
        implementation(project(":frontend-compose-ui"))
        implementation(compose.desktop.currentOs)

        implementation(libs.dev.decompose)
        implementation(libs.dev.decomposeCompose)
        implementation(libs.dev.mvikotlin)
        implementation(libs.dev.mvikotlinMain)
        implementation(libs.dev.mvikotlinCoroutines)
        implementation(libs.dev.mvikotlinLogging)

        implementation(libs.dev.kmlogging)
        implementation(libs.dev.logback)

        implementation(project.dependencies.platform(libs.dev.koinBom))
        implementation("io.insert-koin:koin-core")
      }
    }
  }
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
  }
}

compose.desktop {
  application {
    mainClass = "mikufan.cx.conduit.frontend.app.desktop.MainKt"
  }
}
