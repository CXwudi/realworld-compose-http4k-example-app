plugins {
  alias(libs.plugins.kotlinJvm)
  alias(libs.plugins.serialization)
  application
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
  }
}

kotlin {
  compilerOptions {
    javaParameters = true
    freeCompilerArgs = freeCompilerArgs.get() + listOf("-Xjsr305=strict") // enable strict null check
  }
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation("mikufan.cx.conduit:conduit-common")
  implementation(libs.dev.serializationJson)
  implementation(platform(libs.dev.koinBom))
  implementation("io.insert-koin:koin-core")
  implementation("io.insert-koin:koin-logger-slf4j")
  implementation(libs.dev.backend.hoplite.yaml)


  // https://toolbox.http4k.org/stack/dD1BQU1BWlFETEFTOEItQU1sQTRZRDZBUHBCTEFGRmcmYz1NYWluJnA9bWlrdWZhbi5jeC5jb25kdWl0LmJhY2tlbmQ
  implementation(platform(libs.dev.backend.http4kBom))
  implementation("org.http4k:http4k-core")
  implementation("org.http4k:http4k-server-jetty")
  implementation("org.http4k:http4k-format-kotlinx-serialization")
  implementation("org.http4k:http4k-metrics-micrometer")
  implementation(platform(libs.dev.backend.exposedBom))
  implementation("org.jetbrains.exposed:exposed-core")
  implementation("org.jetbrains.exposed:exposed-dao")
  implementation("org.jetbrains.exposed:exposed-jdbc")
  implementation(libs.dev.backend.postgresql)
  implementation(libs.dev.backend.hikari)
  implementation(libs.dev.backend.inlineLogging)
  implementation(libs.dev.backend.slf4j)
  runtimeOnly(libs.dev.backend.logback)

  testImplementation("io.insert-koin:koin-test-junit5")
  testImplementation(libs.dev.backend.kotestKoin)
  testImplementation("org.http4k:http4k-testing-approval")
  testImplementation("org.http4k:http4k-testing-hamkrest")
  testImplementation("org.http4k:http4k-testing-kotest")
  testImplementation(platform(libs.dev.backend.kotestBom))
  testImplementation("io.kotest:kotest-runner-junit5")
  testImplementation(platform(libs.dev.backend.junitBom))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testImplementation("org.junit.jupiter:junit-jupiter-engine")
  testImplementation(libs.dev.backend.mockk)
}

application {
  mainClass.set("mikufan.cx.conduit.backend.MainKt")
  executableDir = ""
}

tasks.named<org.gradle.jvm.application.tasks.CreateStartScripts>("startScripts") {
  // use wildcard to include all jars in lib folder,
  // instead of default behavior of listing each jar name and version explicitly,
  // so that this enables hot swapping of jars in lib folder
  classpath = files("lib/*")
}

tasks.test {
  useJUnitPlatform()
}
