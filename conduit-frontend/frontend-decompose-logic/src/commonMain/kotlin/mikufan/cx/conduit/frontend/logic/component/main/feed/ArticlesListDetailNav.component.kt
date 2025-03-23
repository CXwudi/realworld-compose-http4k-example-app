package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.navigate
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
  val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList,
      *, ArticlesListDetailNavComponentChild.ArticleDetail,
      Nothing, Nothing>>

  fun setWidestAllowedMode(mode: ChildPanelsMode)
}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultArticlesListDetailNavComponent(
  componentContext: ComponentContext,
  private val searchFilter: ArticlesSearchFilter,
  private val articlesListComponentFactory: ArticlesListComponentFactory,
) : ArticlesListDetailNavComponent, ComponentContext by componentContext {

  private val panelNavigation =
    PanelsNavigation<Config.ArticlesList, Config.ArticleDetail, Nothing>()

  private val _widestAllowedMode: MutableStateFlow<ChildPanelsMode> =
    MutableStateFlow(ChildPanelsMode.SINGLE)
  val widestAllowedMode: StateFlow<ChildPanelsMode> = _widestAllowedMode

  init {
    coroutineScope().launch {
      expandToWidestAllowedMode()
    }
  }

  private suspend fun expandToWidestAllowedMode() {
    widestAllowedMode.collectLatest { widestMode ->
      // Using navigate function so that the transformation is happened in sequence, no race condition
      panelNavigation.navigate { oldPanels ->
        if (oldPanels.mode == widestMode) return@navigate oldPanels
        when {
          widestMode == ChildPanelsMode.SINGLE -> oldPanels.copy(mode = ChildPanelsMode.SINGLE)
          widestMode == ChildPanelsMode.DUAL && oldPanels.details != null -> oldPanels.copy(mode = ChildPanelsMode.DUAL)
          else -> {
            if (widestMode == ChildPanelsMode.TRIPLE) {
              log.warn { "Triple mode is not supported in this ArticlesListDetailNavComponent" }
            }
            oldPanels
          }
        }
      }
    }
  }

  @OptIn(ExperimentalSerializationApi::class)
  override val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList,
      *, ArticlesListDetailNavComponentChild.ArticleDetail,
      Nothing, Nothing>> =
    customChildPanels(
      source = panelNavigation,
      key = "ArticlesListDetailPanel",
      serializers = Config.ArticlesList.serializer() to Config.ArticleDetail.serializer(),
      initialPanels = { Panels(Config.ArticlesList, Config.ArticleDetail) },
      handleBackButton = true,
      mainFactory = ::mainComponent,
      detailsFactory = ::detailComponent,
    )

  override fun setWidestAllowedMode(mode: ChildPanelsMode) {
    log.debug { "Try to set widest mode to $mode" }
    _widestAllowedMode.value = mode
  }

  private fun mainComponent(
    config: Config.ArticlesList,
    componentContext: ComponentContext
  ): ArticlesListDetailNavComponentChild.ArticlesList {
    return ArticlesListDetailNavComponentChild.ArticlesList(
      articlesListComponentFactory.create(componentContext, searchFilter)
    )
  }

  private fun detailComponent(
    config: Config.ArticleDetail,
    componentContext: ComponentContext
  ): ArticlesListDetailNavComponentChild.ArticleDetail {
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
  private val articlesListComponentFactory: ArticlesListComponentFactory,
) {
  fun create(
    componentContext: ComponentContext,
    searchFilter: ArticlesSearchFilter = ArticlesSearchFilter()
  ) = DefaultArticlesListDetailNavComponent(
    componentContext = componentContext,
    searchFilter = searchFilter,
    articlesListComponentFactory = articlesListComponentFactory,
  )
}

private val log = KotlinLogging.logger { }
