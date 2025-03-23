package mikufan.cx.conduit.frontend.logic.component.main.feed

import kotlinx.serialization.Serializable

sealed interface ArticlesListDetailNavComponentChild {
  data class ArticlesList(
    val component: ArticlesListComponent,
  ) : ArticlesListDetailNavComponentChild
  data object ArticleDetail : ArticlesListDetailNavComponentChild
}

@Serializable
data class ArticlesSearchFilter(
  val tag: String? = null,
  val author: String? = null,
  val favoritedByUsername: String? = null,
) {
  val serializedName: String
    get() = "tag:$tag,author:$author,favoritedByUsername:$favoritedByUsername"
}