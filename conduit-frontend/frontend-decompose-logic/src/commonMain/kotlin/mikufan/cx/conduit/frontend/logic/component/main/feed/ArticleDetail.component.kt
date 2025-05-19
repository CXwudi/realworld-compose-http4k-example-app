package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import mikufan.cx.conduit.frontend.logic.component.util.LabelEmitter
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

/**
 * Component for the article detail page
 * TODO: Complete this component once the store is created
 */
interface ArticleDetailComponent : MviComponent<ArticleDetailIntent, ArticleDetailState>, LabelEmitter<ArticleDetailLabel> {
}

class DefaultArticleDetailComponent(
  componentContext: ComponentContext,
  preloadedArticleInfo: PreloadedArticleInfo,
) : ArticleDetailComponent, ComponentContext by componentContext {
  // TODO: Add store, using slug from param

  override val state: StateFlow<ArticleDetailState> =
    MutableStateFlow(ArticleDetailState.Preloaded(preloadedArticleInfo))

  override fun send(intent: ArticleDetailIntent) {
    TODO("Not yet implemented")
  }

  override val labels: Flow<ArticleDetailLabel>
    get() = TODO("Not yet implemented")

}

class ArticleDetailComponentFactory {
  fun create(componentContext: ComponentContext, preloadedArticleInfo: PreloadedArticleInfo) =
    DefaultArticleDetailComponent(componentContext, preloadedArticleInfo)
}
