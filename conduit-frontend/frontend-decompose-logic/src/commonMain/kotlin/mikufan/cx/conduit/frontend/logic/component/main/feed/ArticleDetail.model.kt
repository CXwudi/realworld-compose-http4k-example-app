package mikufan.cx.conduit.frontend.logic.component.main.feed

import kotlinx.serialization.Serializable


sealed interface ArticleDetailIntent {
  data object BackToList : ArticleDetailIntent
}

data class ArticleDetailState(
  val info: CommonArticleInfo
)

sealed interface CommonArticleInfo {
  val authorThumbnail: String?
  val authorUsername: String
  val title: String
  val slug: String
}

@Serializable
data class PreloadedArticleInfo(
  override val authorThumbnail: String?,
  override val authorUsername: String,
  override val title: String,
  override val slug: String,
) : CommonArticleInfo

data class FullArticleInfo(
  override val authorThumbnail: String?,
  override val authorUsername: String,
  override val title: String,
  val description: String,
  val bodyMarkdown: String,
  val tags: List<String>,
  val createdAt: String,
  override val slug: String,
) : CommonArticleInfo

sealed interface ArticleDetailLabel {
  data object BackToList : ArticleDetailLabel
  data class Failure(
    val exception: Exception? = null,
    val message: String,
  ) : ArticleDetailLabel
}
