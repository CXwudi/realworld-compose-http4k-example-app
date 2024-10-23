package mikufan.cx.conduit.frontend.logic.service.landing

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLBuilder
import io.ktor.http.appendEncodedPathSegments
import io.ktor.http.isSuccess
import mikufan.cx.conduit.common.ArticlesRsp


class LandingService(
  private val httpClient: HttpClient,
) {

  suspend fun checkAccessibility(url: String): Result<Unit> = try {
    val httpResponse = httpClient.get {
      url {
        URLBuilder(url).appendEncodedPathSegments("articles")
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