package mikufan.cx.conduit.frontend.logic.service.landing

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import mikufan.cx.conduit.common.ArticlesRsp


interface LandingService {
  /**
   * Check accessibility of the given [url].
   *
   * @throws Exception if in any case the [url] is not accessible.
   */
  suspend fun checkAccessibility(url: String)
}

class DefaultLandingService(
  private val httpClient: HttpClient,
) : LandingService {

  companion object {
    private val ALLOWED_PROTOCOLS = setOf("http", "https")
  }

  override suspend fun checkAccessibility(url: String) {
    val normalizedUrl = when {
      // Extract and validate existing protocol
      url.contains("://") -> {
        val protocol = url.substringBefore("://")
        if (protocol.lowercase() !in ALLOWED_PROTOCOLS) {
          error("Invalid protocol: $protocol. Only HTTP/HTTPS allowed")
        }
        url
      }
      // Add default http protocol if missing
      else -> "http://$url"
    }.removeSuffix("/")

    // send a request to a public api to check if the url is accessible
    val httpResponse = httpClient.get {
      url {
        takeFrom(normalizedUrl).appendEncodedPathSegments("articles")
      }
      timeout {
        requestTimeoutMillis = 10000
      }
    }

    if (!httpResponse.status.isSuccess()) {
      error("Failed with status ${httpResponse.status}: ${httpResponse.bodyAsText()}")
    }

    // make sure the response is what we are looking for
    val articlesRsp = httpResponse.body<ArticlesRsp>()
    if (articlesRsp.articlesCount == 0 && articlesRsp.articles.isEmpty()) {
      return
    } else if (articlesRsp.articlesCount > 0 && articlesRsp.articles.isNotEmpty() && articlesRsp.articles.first().body.isNotEmpty()) {
      return
    } else {
      error("Invalid response")
    }
  }

}