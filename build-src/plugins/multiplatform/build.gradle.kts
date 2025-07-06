plugins {
  `kotlin-dsl`
  alias(libs.plugins.kotlinJvm)
}

dependencies {
  implementation(project(":version-catalog-util"))
  implementation(libs.pluginDep.kotlin)
  implementation(libs.pluginDep.android)
  implementation(libs.pluginDep.serialization)
  implementation(libs.pluginDep.mokkery)
  implementation(libs.pluginDep.compose)
  implementation(libs.pluginDep.kotlinCompose)
}