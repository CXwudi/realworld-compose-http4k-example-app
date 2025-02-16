package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.setMode
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.custom.customChildPanels

/**
 * Naming is a bit confusing
 *
 * - Articles List Detail: this is a component for showing the list of articles, and optionally the content of the article
 * - Navigation: this component is handling the list detail navigation between the list of articles and the detail of the article.
 *   The list component and the detail component are two separate components.
 * - Component: this is a decompose component.
 *
 * The ArticlesListDetailPanelNavComponent is the component that handles the navigation between the list of articles and the detail of the article.
 */
@OptIn(ExperimentalDecomposeApi::class)
interface ArticlesListDetailNavComponent {
  val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList, *, ArticlesListDetailNavComponentChild.ArticleDetail, Unit, Unit>>

  fun setMode(mode: ChildPanelsMode)
}

sealed interface ArticlesListDetailNavComponentChild {
  data object ArticlesList : ArticlesListDetailNavComponentChild
  data object ArticleDetail : ArticlesListDetailNavComponentChild
}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultArticlesListDetailNavComponent(
  componentContext: ComponentContext,
) : ArticlesListDetailNavComponent, ComponentContext by componentContext {


  private val panelNavigation = PanelsNavigation<Config.ArticlesList, Config.ArticleDetail, Nothing>()

  @OptIn(ExperimentalSerializationApi::class)
  override val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList, *, ArticlesListDetailNavComponentChild.ArticleDetail, Unit, Unit>> =
    customChildPanels(
      source = panelNavigation,
      serializers = Config.ArticlesList.serializer() to Config.ArticleDetail.serializer(),
      initialPanels = { Panels(Config.ArticlesList) },
      handleBackButton = true,
      mainFactory = ::mainComponent,
      detailsFactory = ::detailComponent,
    )

  override fun setMode(mode: ChildPanelsMode) = panelNavigation.setMode(mode)

  private fun mainComponent(config: Config.ArticlesList, componentContext: ComponentContext): ArticlesListDetailNavComponentChild.ArticlesList {
    return ArticlesListDetailNavComponentChild.ArticlesList
  }

  private fun detailComponent(config: Config.ArticleDetail, componentContext: ComponentContext): ArticlesListDetailNavComponentChild.ArticleDetail {
    return ArticlesListDetailNavComponentChild.ArticleDetail
  }


  @Serializable
  sealed interface Config {
    @Serializable
    data object ArticlesList : Config
    @Serializable
    data object ArticleDetail : Config
  }
}


class ArticlesPanelNavComponentFactory(

) {
  fun create(componentContext: ComponentContext) = DefaultArticlesListDetailNavComponent(
    componentContext = componentContext,
  )
}
