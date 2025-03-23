package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutorScope
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import io.github.oshai.kotlinlogging.KotlinLogging
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

  /**
   * Execute an operation in a coroutine with standardized error handling.
   *
   * @param block The operation to execute inside the try-catch block
   */
  private inline fun CoroutineExecutorScope<
      ArticlesListState, Msg, ArticlesListAction, ArticlesListLabel>.executeWithErrorHandling(
    crossinline execution: suspend () -> Unit,
    crossinline onFailure: (Throwable) -> Unit
  ) = launch {
    try {
      execution()
    } catch (t: Throwable) {
      rethrowIfShouldNotBeHandled(t) {
        onFailure(t)

      }
    }
  }


  private fun createExecutor(searchFilter: ArticlesSearchFilter) =
    coroutineExecutorFactory<ArticlesListIntent, ArticlesListAction, ArticlesListState, Msg, ArticlesListLabel>(
      mainContext = mainDispatcher
    ) {
      onAction<ArticlesListAction.LoadInitialArticles> {
        executeWithErrorHandling(
          execution = {
            val initialArticleInfos = withContext(Dispatchers.Default) {
              articlesListService.getInitialArticles(searchFilter)
            }
            dispatch(Msg.LoadedInitialArticles(initialArticleInfos))
          },
          onFailure = { t ->
            publish(
              ArticlesListLabel.Failure(
                exception = if (t is Exception) t else null,
                message = t.message ?: "Unknown error"
              )
            )
          }
        )
      }

      onIntent<ArticlesListIntent.LoadMore> {
        executeWithErrorHandling(
          execution = {
            when (val state = state()) {
              is ArticlesListState.Loaded -> {
                dispatch(Msg.SetToLoadingMore)
                val moreInfo = withContext(Dispatchers.Default) {
                  articlesListService.getArticles(
                    searchFilter,
                    offset = state.collectedThumbInfos.size
                  )
                }
                dispatch(Msg.AddMoreArticles(moreInfo))
              }

              is ArticlesListState.Loading -> {
                log.warn { "Loading more articles when state is not loaded, should not happen and discarding" }
              }
            }
          },
          onFailure = { t ->
            dispatch(Msg.LoadingMoreAborted)
            publish(
              ArticlesListLabel.Failure(
                exception = if (t is Exception) t else null,
                message = t.message ?: "Unknown error"
              )
            )
          }
        )
      }
    }

  private fun createBootstrapper() =
    coroutineBootstrapper<ArticlesListAction>(mainContext = mainDispatcher) {
      dispatch(ArticlesListAction.LoadInitialArticles)
    }

  private fun createReducer() = Reducer<ArticlesListState, Msg> { msg ->
    when (msg) {
      is Msg.LoadedInitialArticles -> ArticlesListState.Loaded(
        msg.articleInfos,
        isLoadingMore = false
      )

      is Msg.SetToLoadingMore -> {
        when (this) {
          is ArticlesListState.Loaded -> ArticlesListState.Loaded(
            this.collectedThumbInfos,
            isLoadingMore = true
          )

          is ArticlesListState.Loading -> {
            log.warn { "Setting to loading more when state is not loaded, should not happen and discarding" }
            this
          }
        }
      }

      is Msg.AddMoreArticles -> {
        when (this) {
          is ArticlesListState.Loaded -> ArticlesListState.Loaded(
            this.collectedThumbInfos + msg.articleInfos,
            isLoadingMore = false
          )

          is ArticlesListState.Loading -> {
            log.warn { "Adding more articles when state is not loaded, should not happen" }
            this
          }
        }
      }

      is Msg.LoadingMoreAborted -> {
        when (this) {
          is ArticlesListState.Loaded -> ArticlesListState.Loaded(
            this.collectedThumbInfos,
            isLoadingMore = false
          )

          is ArticlesListState.Loading -> {
            log.warn { "Aborting loading more when state is not loaded, should not happen" }
            this
          }
        }
      }
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
    data object SetToLoadingMore : Msg
    data class AddMoreArticles(val articleInfos: List<ArticleInfo>) : Msg
    data object LoadingMoreAborted : Msg
  }
}

private val log = KotlinLogging.logger {}
