package mikufan.cx.conduit.frontend.logic.component.main.feed


sealed interface ArticlesListIntent {
  data object LoadMore : ArticlesListIntent
}

sealed interface ArticlesListState {
  data object Loading : ArticlesListState
  data class Loaded(
    val collectedThumbInfos: List<ArticleInfo>,
    val isLoadingMore: Boolean,
  ) : ArticlesListState
}

data class ArticleInfo(
  val authorThumbnail: String?,
  val authorUsername: String,
  val title: String,
  val description: String,
  val tags: List<String>,
  val createdAt: String,
  val slug: String,
)

sealed interface ArticlesListAction {
  data object LoadInitialArticles : ArticlesListAction
}

sealed interface ArticlesListLabel {
  data class Failure(
    val exception: Exception?,
    val message: String,
  ) : ArticlesListLabel
}
