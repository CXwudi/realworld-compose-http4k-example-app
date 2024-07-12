import com.github.gmazzo.buildconfig.BuildConfigClassSpec

plugins {
  `kotlin-dsl`
  alias(libs.plugins.buildConfig)
}

buildConfig {
  generateAtSync = false
  useKotlinOutput { internalVisibility = false }
  forClass(packageName = "my.util", className = "Versions") {
    buildConfigField(Int::class.java, "Java", libs.versions.java.map { it.toInt() })
  }
  forClass(packageName = "my.util", className = "Libs") {
    // Serialization
    buildConfigStringField("SerializationJson", libs.dev.serializationJson)

    // Coroutines
    buildConfigStringField("CoroutinesBom", libs.dev.coroutinesBom)

    // Essenty
    buildConfigStringField("EssentyLifecycleCoroutines", libs.dev.essentyLifecycleCoroutines)

    // Decompose
    buildConfigStringField("Decompose", libs.dev.decompose)
    buildConfigStringField("DecomposeCompose", libs.dev.decomposeCompose)

    // MVI Kotlin
    buildConfigStringField("MviKotlin", libs.dev.mvikotlin)
    buildConfigStringField("MviKotlinMain", libs.dev.mvikotlinMain)
    buildConfigStringField("MviKotlinCoroutines", libs.dev.mvikotlinCoroutines)
    buildConfigStringField("MviKotlinLogging", libs.dev.mvikotlinLogging)

    // Logging
    buildConfigStringField("KmLogging", libs.dev.kmlogging)
    buildConfigStringField("Logback", libs.dev.logback)

    // Koin
    buildConfigStringField("KoinBom", libs.dev.koinBom)

    // Kotlin Wrapper
    buildConfigStringField("KotlinWrapper", libs.dev.kotlinWrapper)

    // AndroidX
    buildConfigStringField("AndroidXCoreKtx", libs.dev.androidx.coreKtx)
    buildConfigStringField("AndroidXLifecycleKtx", libs.dev.androidx.lifecycleKtx)
    buildConfigStringField("AndroidXAppCompat", libs.dev.androidx.appcompat)
    buildConfigStringField("AndroidXActivityCompose", libs.dev.androidx.activityCompose)
  }
}

fun BuildConfigClassSpec.buildConfigStringField(name: String, version: Provider<*>) {
  buildConfigField(String::class.java, name, version.map { "$it" })
}