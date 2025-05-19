package mikufan.cx.conduit.frontend.logic.component.main.feed

import kotlinx.serialization.Serializable


sealed interface ArticlesListIntent {
  data object LoadMore : ArticlesListIntent
  data class SelectArticle(val articleInfo: ArticleInfo) : ArticlesListIntent
}

data class ArticlesListState(
  val collectedThumbInfos: List<ArticleInfo> = emptyList(),
  val loadMoreState: LoadMoreState = LoadMoreState.Loaded
)

enum class LoadMoreState { Loading, Loaded }

@Serializable
data class ArticleInfo(
  val authorThumbnail: String?,
  val authorUsername: String,
  val title: String,
  val description: String,
  val tags: List<String>,
  val createdAt: String,
  val slug: String,
) {
  fun toPreloadedInfo(): PreloadedArticleInfo = PreloadedArticleInfo(
    authorThumbnail = authorThumbnail,
    authorUsername = authorUsername,
    title = title,
    slug = slug,
  )
}

sealed interface ArticlesListLabel {
  data class Failure(
    val exception: Exception?,
    val message: String,
  ) : ArticlesListLabel
  data class OpenArticle(val articleInfo: ArticleInfo) : ArticlesListLabel
}
