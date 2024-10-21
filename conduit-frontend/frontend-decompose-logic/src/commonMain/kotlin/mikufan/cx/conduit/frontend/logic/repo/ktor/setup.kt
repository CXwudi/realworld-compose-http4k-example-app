package mikufan.cx.conduit.frontend.logic.repo.ktor

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

fun createDefaultHttpClient(): HttpClient = setupKtorClient {
  install(ContentNegotiation) {
    json()
  }
}

expect fun setupKtorClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient