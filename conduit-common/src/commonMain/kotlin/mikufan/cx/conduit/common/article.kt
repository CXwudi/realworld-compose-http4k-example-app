package mikufan.cx.conduit.common

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class CreateArticleReq(
  val article: CreateArticleDto
)

@Serializable
data class CreateArticleDto(
  val title: String,
  val description: String,
  val body: String,
  val tagList: List<String>
)

/**
 * Response of the list articles request
 */
@Serializable
data class ArticlesRsp(
  val articles: List<ArticleDto>,
  val articlesCount: Int
)

/**
 * Response of a single article request
 */
@Serializable
data class ArticleRsp(
  val article: ArticleDto
)

@Serializable
data class ArticleDto(
  val author: AuthorDto,
  /**
   * null body means the API doesn't return the body during the list articles request
   */
  val body: String? = null,
  val createdAt: Instant,
  val description: String,
  val favorited: Boolean,
  val favoritesCount: Int,
  val slug: String,
  val tagList: List<String>,
  val title: String,
  val updatedAt: Instant
)

@Serializable
data class AuthorDto(
  val bio: String?,
  val following: Boolean,
  val image: String?,
  val username: String
)

object ArticleDtoUtils {
  fun createArticleReq(
    title: String,
    description: String,
    body: String,
    tagList: List<String>
  ) = CreateArticleReq(
    article = CreateArticleDto(
      title = title,
      description = description,
      body = body,
      tagList = tagList
    )
  )
}