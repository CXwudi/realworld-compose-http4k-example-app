plugins {
  `kotlin-dsl`
}

dependencies {
  implementation(project(":version-catalog-util"))
  implementation(libs.pluginDep.kotlin)
  implementation(libs.pluginDep.android)
  implementation(libs.pluginDep.serialization)
  implementation(libs.pluginDep.mockkery)
//  implementation(libs.pluginDep.compose)
//  implementation(libs.pluginDep.kotlinCompose)
}