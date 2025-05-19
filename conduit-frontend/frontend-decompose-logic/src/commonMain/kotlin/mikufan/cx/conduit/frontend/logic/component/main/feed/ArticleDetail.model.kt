package mikufan.cx.conduit.frontend.logic.component.main.feed

import kotlinx.serialization.Serializable


sealed interface ArticleDetailIntent {
  data object BackToList : ArticleDetailIntent
  data object LoadArticle : ArticleDetailIntent
}

sealed interface ArticleDetailState {
  data class Preloaded(val info: PreloadedArticleInfo): ArticleDetailState
  data class Loaded(val info: FullArticleInfo): ArticleDetailState
}

@Serializable
data class PreloadedArticleInfo(
  val authorThumbnail: String?,
  val authorUsername: String,
  val title: String,
  val slug: String,
)

data class FullArticleInfo(
  val authorThumbnail: String?,
  val authorUsername: String,
  val title: String,
  val description: String,
  val bodyMarkdown: String,
  val tags: List<String>,
  val createdAt: String,
  val slug: String,
)

sealed interface ArticleDetailLabel {
  data object BackToList : ArticleDetailLabel
  data class Failure(
    val exception: Exception? = null,
    val message: String,
  ) : ArticleDetailLabel
}
