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

/**
 * Component for the article detail page
 */
interface ArticleDetailComponent : 
  MviComponent<ArticleDetailIntent, ArticleDetailState>,
  LabelEmitter<ArticleDetailLabel>

class DefaultArticleDetailComponent(
  componentContext: ComponentContext,
  basicInfo: ArticleBasicInfo,
  articleDetailStoreFactory: ArticleDetailStoreFactory,
  private val onBackToList: () -> Unit,
) : ArticleDetailComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore {
    articleDetailStoreFactory.createStore(basicInfo, autoInit = true)
  }

  override val state: StateFlow<ArticleDetailState> = store.stateFlow(coroutineScope())
  override val labels: Flow<ArticleDetailLabel> = store.labels

  init {
    coroutineScope().launch {
      store.labels.collectLatest { label ->
        when (label) {
          is ArticleDetailLabel.BackToList -> onBackToList()
          else -> Unit // Ignore other labels
        }
      }
    }
  }

  override fun send(intent: ArticleDetailIntent) = store.accept(intent)
}

class ArticleDetailComponentFactory(
  private val articleDetailStoreFactory: ArticleDetailStoreFactory,
) {
  fun create(
    componentContext: ComponentContext,
    basicInfo: ArticleBasicInfo,
    onBackToList: () -> Unit
  ) =
    DefaultArticleDetailComponent(componentContext, basicInfo, articleDetailStoreFactory, onBackToList)
}
