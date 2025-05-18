package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.util.LabelEmitter
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

interface ArticlesListComponent :
  MviComponent<ArticlesListIntent, ArticlesListState>,
  LabelEmitter<ArticlesListLabel>

class DefaultArticlesListComponent(
  componentContext: ComponentContext,
  searchFilter: ArticlesSearchFilter,
  articlesListStoreFactory: ArticlesListStoreFactory,
  private val onOpenArticle: (String) -> Unit,
) : ArticlesListComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { articlesListStoreFactory.create(searchFilter) }

  override val state: StateFlow<ArticlesListState> = store.stateFlow(coroutineScope())
  override val labels: Flow<ArticlesListLabel> = store.labels

  override fun send(intent: ArticlesListIntent) = store.accept(intent)

  init {
    coroutineScope().launch {
      listenToNavigationLabel()
    }
  }

  private suspend fun listenToNavigationLabel() {
    store.labels.collectLatest { label ->
      when (label) {
        is ArticlesListLabel.OpenArticle -> onOpenArticle(label.slug)
        else -> Unit // Ignore other labels
      }
    }
  }
}

class ArticlesListComponentFactory(
  private val articlesListStoreFactory: ArticlesListStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    searchFilter: ArticlesSearchFilter,
    onOpenArticle: (String) -> Unit,
  ) = DefaultArticlesListComponent(
    componentContext = componentContext,
    searchFilter = searchFilter,
    articlesListStoreFactory = articlesListStoreFactory,
    onOpenArticle = onOpenArticle,
  )
}
