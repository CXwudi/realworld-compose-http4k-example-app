package mikufan.cx.conduit.frontend.logic.component.main.feed


sealed interface ArticleDetailIntent {
  data object BackToList : ArticleDetailIntent
  data object LoadArticle : ArticleDetailIntent
}

data class ArticleDetailState(
  // TODO: still decided that not a good idea to re-use list model here,
  // create a two state, one for preloaded, one for fully loaded
  val info: ArticleInfo,
  // null body means not loaded yet
  val body: String? = null,
)

sealed interface ArticleDetailLabel {
  data object BackToList : ArticleDetailLabel
  data class Failure(
    val exception: Exception? = null,
    val message: String,
  ) : ArticleDetailLabel
}
