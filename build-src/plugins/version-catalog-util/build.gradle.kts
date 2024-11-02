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
    buildConfigStringField("EssentyLifecycleCoroutines", libs.dev.frontend.essentyLifecycleCoroutines)

    // Decompose
    buildConfigStringField("Decompose", libs.dev.frontend.decompose)
    buildConfigStringField("DecomposeCompose", libs.dev.frontend.decomposeCompose)

    // MVI Kotlin
    // likely this is only needed for decompose-logic module
//    buildConfigStringField("MviKotlin", libs.dev.frontend.mvikotlin)
//    buildConfigStringField("MviKotlinMain", libs.dev.frontend.mvikotlinMain)
//    buildConfigStringField("MviKotlinCoroutines", libs.dev.frontend.mvikotlinCoroutines)
//    buildConfigStringField("MviKotlinLogging", libs.dev.frontend.mvikotlinLogging)

    // Logging
    buildConfigStringField("KotlinLogging", libs.dev.kotlinLogging)
    buildConfigStringField("Slf4jAndroid", libs.dev.frontend.slf4jAndroid)
    buildConfigStringField("Logback", libs.dev.backend.logback)

    // Koin
    buildConfigStringField("KoinBom", libs.dev.koinBom)

    // Kotlin Wrapper
    buildConfigStringField("KotlinWrapper", libs.dev.frontend.kotlinWrapper)

    // AndroidX
    buildConfigStringField("AndroidXCoreKtx", libs.dev.frontend.androidx.coreKtx)
    buildConfigStringField("AndroidXLifecycleKtx", libs.dev.frontend.androidx.lifecycleKtx)
    buildConfigStringField("AndroidXAppCompat", libs.dev.frontend.androidx.appcompat)
    buildConfigStringField("AndroidXActivityCompose", libs.dev.frontend.androidx.activityCompose)
  }
}

fun BuildConfigClassSpec.buildConfigStringField(name: String, version: Provider<*>) {
  buildConfigField(String::class.java, name, version.map { "$it" })
}