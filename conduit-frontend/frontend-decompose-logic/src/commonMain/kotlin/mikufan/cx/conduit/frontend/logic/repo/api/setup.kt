package mikufan.cx.conduit.frontend.logic.repo.api

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLBuilder
import io.ktor.http.appendEncodedPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import mikufan.cx.conduit.frontend.logic.repo.api.util.ConduitResponseConverterFactory
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigState

fun defaultJson(): Json = Json {
  ignoreUnknownKeys = true
}

fun createDefaultHttpClient(userConfigKStore: UserConfigKStore, json: Json): HttpClient =
  setupKtorClient {
    install(ContentNegotiation) {
      json(json)
    }

    install(createBaseUrlAndTokenProvidingPlugin(userConfigKStore))
  }

expect fun setupKtorClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun createKtorfit(
  httpClient: HttpClient,
  conduitResponseConverterFactory: ConduitResponseConverterFactory
): Ktorfit =
  Ktorfit.Builder()
    .httpClient(httpClient)
    .converterFactories(conduitResponseConverterFactory)
    .build()

/**
 * taken the idea from https://bbasoglu.medium.com/part-1-how-to-change-base-url-on-runtime-in-an-android-project-1d7607bbfa48
 *
 * Add a plugin to the HttpClient that intercepts change the base URL and token from the user config kstore.
 */
fun createBaseUrlAndTokenProvidingPlugin(userConfigKStore: UserConfigKStore) =
  createClientPlugin("BaseUrlAndTokenProvidingPlugin") {
    onRequest { requestBuilder, _ ->
      val userConfigState = userConfigKStore.userConfigFlow.first()
      if (userConfigState !is UserConfigState.Loaded) {
        throw IllegalStateException("User config is not loaded when making API call")
      }

      userConfigState.url?.let { baseUrl ->
        requestBuilder.url { reqUrlBuilder ->
          URLBuilder(baseUrl).apply {
            appendEncodedPathSegments(reqUrlBuilder.encodedPathSegments)
            parameters.appendAll(reqUrlBuilder.parameters.build())
          }
        }
      }

      userConfigState.token?.let {
        requestBuilder.headers["Authorization"] = "Token $it"
      }
    }
  }