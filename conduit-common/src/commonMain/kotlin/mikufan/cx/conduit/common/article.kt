package mikufan.cx.conduit.common

import kotlinx.serialization.Serializable

@Serializable
data class ArticlesRsp(
  val articles: List<ArticleDto>,
  val articlesCount: Int
)

@Serializable
data class AuthorDto(
  val bio: String?,
  val following: Boolean,
  val image: String?,
  val username: String
)

@Serializable
data class ArticleDto(
  val author: AuthorDto,
  val body: String,
  val createdAt: String,
  val description: String,
  val favorited: Boolean,
  val favoritesCount: Int,
  val slug: String,
  val tagList: List<String>,
  val title: String,
  val updatedAt: String
)