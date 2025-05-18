package mikufan.cx.conduit.frontend.logic.component.main.feed


sealed interface ArticlesListIntent {
  data object LoadMore : ArticlesListIntent
  data class SelectArticle(val slug: String) : ArticlesListIntent
}

data class ArticlesListState(
  val collectedThumbInfos: List<ArticleInfo> = emptyList(),
  val loadMoreState: LoadMoreState = LoadMoreState.Loaded
)

enum class LoadMoreState { Loading, Loaded }

data class ArticleInfo(
  val authorThumbnail: String?,
  val authorUsername: String,
  val title: String,
  val description: String,
  val tags: List<String>,
  val createdAt: String,
  val slug: String,
)

sealed interface ArticlesListLabel {
  data class Failure(
    val exception: Exception?,
    val message: String,
  ) : ArticlesListLabel
  data class OpenArticle(val slug: String) : ArticlesListLabel
}
