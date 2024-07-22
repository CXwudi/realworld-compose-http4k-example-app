plugins {
  // do all the declaration here to avoid warning
  alias(libs.plugins.kotlinMultiplatform) apply false
  alias(libs.plugins.kotlinJvm) apply false
  alias(libs.plugins.androidLibrary) apply false
  alias(libs.plugins.androidApplication) apply false
  alias(libs.plugins.compose) apply false
  alias(libs.plugins.kotlinCompose) apply false
}