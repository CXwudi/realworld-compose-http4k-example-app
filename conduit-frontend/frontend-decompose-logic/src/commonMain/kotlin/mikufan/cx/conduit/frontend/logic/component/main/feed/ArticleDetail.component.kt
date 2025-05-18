package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.decompose.ComponentContext

/**
 * Component for the article detail page
 * TODO: Complete this component with proper implementation for article content retrieval and display
 */
interface ArticleDetailComponent {
  val slug: String
}

class DefaultArticleDetailComponent(
  componentContext: ComponentContext,
  override val slug: String
) : ArticleDetailComponent, ComponentContext by componentContext {
  // TODO: Add store
}

class ArticleDetailComponentFactory {
  fun create(componentContext: ComponentContext, slug: String) =
    DefaultArticleDetailComponent(componentContext, slug)
}
