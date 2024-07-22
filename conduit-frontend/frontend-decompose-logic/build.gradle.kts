plugins {
  id("my.kmp-frontend-library")
  alias(libs.plugins.sqldelight)
}

android {
  namespace = "mikufan.cx.conduit.frontend.logic"
}

kotlin {
  sourceSets {
    commonMain.dependencies {
      // add dependencies that are specific for this decompose logic module
      // dependencies used in both this module and compose ui module are extracted into the precompiled script plugin
      implementation(libs.dev.sqldelightCoroutines)
      implementation(libs.dev.kstore)
    }

    // and platform specific dependencies only used in this module

    commonJvmMain.dependencies {
      implementation(libs.dev.kstore.file)
    }

    androidMain.dependencies {
      implementation(libs.dev.sqldelightDriver.android)
    }

    jvmMain.dependencies {
      implementation(libs.dev.sqldelightDriver.jvm)
    }

    jsMain.dependencies {
      implementation(libs.dev.sqldelightDriver.webWorker)
      implementation(npm("@cashapp/sqldelight-sqljs-worker", libs.versions.sqldelight.get()))
      implementation(npm("sql.js", "1.10.3"))
      implementation(devNpm("copy-webpack-plugin", "12.0.2"))
      implementation(libs.dev.kstore.storage)
    }

    iosMain.dependencies {
      implementation(libs.dev.kstore.file)
      implementation(libs.dev.sqldelightDriver.ios)
    }
  }
}

sqldelight {
  databases {
    create("AppDb") {
      packageName = "mikufan.cx.conduit.frontend.logic.repo.db"
      generateAsync = true
      schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
      migrationOutputDirectory = file("src/commonMain/sqldelight/migrations")
      deriveSchemaFromMigrations = true
      verifyMigrations = true
    }
  }
}