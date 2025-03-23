package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mikufan.cx.conduit.frontend.logic.component.util.rethrowIfShouldNotBeHandled
import mikufan.cx.conduit.frontend.logic.service.main.ArticlesListService

class ArticlesListStoreFactory(
  private val storeFactory: StoreFactory,
  private val articlesListService: ArticlesListService,
  private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
) {

  private fun createExecutor(searchFilter: ArticlesSearchFilter) =
    coroutineExecutorFactory<ArticlesListIntent, ArticlesListAction, ArticlesListState, Msg, ArticlesListLabel>(
      mainContext = mainDispatcher
    ) {
      onAction<ArticlesListAction.LoadInitialArticles> {
        launch {
          try {
            val initialArticleInfos = withContext(Dispatchers.Default) {
              articlesListService.getInitialArticles(searchFilter)
            }
            dispatch(Msg.LoadedInitialArticles(initialArticleInfos))
          } catch (e: Throwable) {
            rethrowIfShouldNotBeHandled(e) {
              publish(ArticlesListLabel.Failure(
                exception = if (e is Exception) e else null,
                message = e.message ?: "Unknown error"
              ))
            }
          }
        }
      }
    }

  private fun createBootstrapper() =
    coroutineBootstrapper<ArticlesListAction>(mainContext = mainDispatcher) {
      dispatch(ArticlesListAction.LoadInitialArticles)
    }

  private fun createReducer() = Reducer<ArticlesListState, Msg> { msg ->
    when (msg) {
      is Msg.LoadedInitialArticles -> ArticlesListState.Loaded(msg.articleInfos, isLoadingMore = false)
    }
  }

  fun create(searchFilter: ArticlesSearchFilter, autoInit: Boolean = true) =
    storeFactory.create(
      name = "ArticlesListStore-${searchFilter.serializedName}",
      initialState = ArticlesListState.Loading,
      bootstrapper = createBootstrapper(),
      executorFactory = createExecutor(searchFilter),
      reducer = createReducer(),
      autoInit = autoInit
    )

  private sealed interface Msg {
    data class LoadedInitialArticles(val articleInfos: List<ArticleInfo>) : Msg
  }
}