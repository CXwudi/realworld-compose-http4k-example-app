package mikufan.cx.conduit.frontend.logic.component.poc

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
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleBasicInfo
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticleDetailComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesListDetailNavComponentChild
import mikufan.cx.conduit.frontend.logic.component.main.feed.ArticlesSearchFilter

/**
 * PoC demonstrating how to replace the custom ArticlesListDetailNavComponent
 * with a standard Decompose implementation using the StandardDecomposeChildPanelsPoC.
 * 
 * This is a drop-in replacement for the original component that:
 * - Uses standard Decompose childPanels() API
 * - Implements dynamic back handler registration
 * - Maintains identical behavior to the original
 * - Handles all three panes (main, detail, extra) properly
 */
@OptIn(ExperimentalDecomposeApi::class)
interface ArticlesListDetailNavComponentPoC {
    val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList,
        *, ArticlesListDetailNavComponentChild.ArticleDetail,
        Nothing, Nothing>>

    fun setWidestAllowedMode(mode: ChildPanelsMode)
}

@OptIn(ExperimentalDecomposeApi::class)
class DefaultArticlesListDetailNavComponentPoC(
    componentContext: ComponentContext,
    private val searchFilter: ArticlesSearchFilter,
    private val articlesListComponentFactory: ArticlesListComponentFactory,
    private val articleDetailComponentFactory: ArticleDetailComponentFactory,
) : ArticlesListDetailNavComponentPoC, ComponentContext by componentContext {

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
            // Using the navigate function so that the transformation is happened in sequence, no race condition
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

    /**
     * CORRECTED: This now uses the standard Decompose childPanels with dynamic back handler.
     * 
     * Key differences from the original implementation:
     * - Uses standardChildPanelsWithBackHandler() instead of customizableBackHandlerChildPanels()
     * - multiModeBackHandling=true enables the equivalent of MultiModeChildPanelsBackHandler
     * - Same behavior but with standard Decompose APIs
     */
    @OptIn(ExperimentalSerializationApi::class)
    override val panels: Value<ChildPanels<*, ArticlesListDetailNavComponentChild.ArticlesList,
        *, ArticlesListDetailNavComponentChild.ArticleDetail,
        Nothing, Nothing>> =
        standardChildPanelsWithBackHandler(
            source = panelNavigation,
            key = "ArticlesListDetailPanel",
            serializers = Config.ArticlesList.serializer() to Config.ArticleDetail.serializer(),
            initialPanels = { Panels(Config.ArticlesList) },
            multiModeBackHandling = true, // This enables the equivalent of MultiModeChildPanelsBackHandler
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
        val listComponent = articlesListComponentFactory.create(
            componentContext = componentContext,
            searchFilter = searchFilter,
            onOpenArticle = { basicInfo ->
                panelNavigation.navigate { panels ->
                    // Set mode to DUAL if widest allowed mode is at least DUAL
                    val newMode = if (_widestAllowedMode.value >= ChildPanelsMode.DUAL) {
                        ChildPanelsMode.DUAL
                    } else {
                        panels.mode
                    }
                    panels.copy(details = Config.ArticleDetail(basicInfo), mode = newMode)
                }
            }
        )
        
        return ArticlesListDetailNavComponentChild.ArticlesList(listComponent)
    }

    private fun detailComponent(
        config: Config.ArticleDetail,
        componentContext: ComponentContext
    ): ArticlesListDetailNavComponentChild.ArticleDetail {
        val detailComponent = articleDetailComponentFactory.create(
            componentContext = componentContext,
            basicInfo = config.basicInfo,
            onBackToList = {
                panelNavigation.navigate { panels ->
                    val targetMode = if (panels.mode > ChildPanelsMode.SINGLE) ChildPanelsMode.SINGLE else panels.mode
                    panels.copy(details = null, mode = targetMode)
                }
            }
        )
        return ArticlesListDetailNavComponentChild.ArticleDetail(detailComponent)
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object ArticlesList : Config

        @Serializable
        data class ArticleDetail(val basicInfo: ArticleBasicInfo) : Config
    }
}

/**
 * Factory for creating the PoC component.
 */
class ArticlesPanelNavComponentPoCFactory(
    private val articlesListComponentFactory: ArticlesListComponentFactory,
    private val articleDetailComponentFactory: ArticleDetailComponentFactory,
) {
    fun create(
        componentContext: ComponentContext,
        searchFilter: ArticlesSearchFilter = ArticlesSearchFilter()
    ) = DefaultArticlesListDetailNavComponentPoC(
        componentContext = componentContext,
        searchFilter = searchFilter,
        articlesListComponentFactory = articlesListComponentFactory,
        articleDetailComponentFactory = articleDetailComponentFactory,
    )
}

private val log = KotlinLogging.logger { }