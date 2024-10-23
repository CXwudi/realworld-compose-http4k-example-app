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
  suspend fun checkAccessibility(url: String): Result<Unit>
}

class DefaultLandingService(
  private val httpClient: HttpClient,
): LandingService {

  override suspend fun checkAccessibility(url: String): Result<Unit> = try {
    val normalizedUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
      url
    } else {
      "http://$url"
    }.removeSuffix("/")
    val httpResponse = httpClient.get {
      url {
        takeFrom(normalizedUrl).appendEncodedPathSegments("articles")
      }
      timeout {
        requestTimeoutMillis = 10000
      }
    }
    if (httpResponse.status.isSuccess()) {
      val articlesRsp = httpResponse.body<ArticlesRsp>()
      if (articlesRsp.articlesCount == 0 && articlesRsp.articles.isEmpty()) {
        Result.success(Unit)
      } else if (articlesRsp.articlesCount > 0 && articlesRsp.articles.isNotEmpty() && articlesRsp.articles.first().body.isNotEmpty()) {
        Result.success(Unit)
      } else {
        Result.failure(Exception("Invalid response"))
      }
    } else {
      Result.failure(Exception("Failed with status ${httpResponse.status}: ${httpResponse.bodyAsText()}"))
    }
  } catch (e: Exception) {
    Result.failure(e)
  }

}