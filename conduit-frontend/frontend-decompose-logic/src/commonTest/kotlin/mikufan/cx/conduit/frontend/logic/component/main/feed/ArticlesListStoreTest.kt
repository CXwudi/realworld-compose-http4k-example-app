package mikufan.cx.conduit.frontend.logic.component.main.feed

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.extensions.coroutines.labelsChannel
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.service.main.ArticlesListService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArticlesListStoreTest {
  private val testDispatcher = StandardTestDispatcher()

  private lateinit var articlesListService: ArticlesListService
  private lateinit var articlesListStore: Store<ArticlesListIntent, ArticlesListState, ArticlesListLabel>
  
  private val defaultSearchFilter = ArticlesSearchFilter()
  private val testArticles = listOf(
    ArticleInfo(
      authorThumbnail = "https://example.com/avatar.png",
      authorUsername = "testuser",
      title = "Test Article",
      description = "Test Description",
      tags = listOf("test", "kotlin"),
      createdAt = "2023-01-01T12:00:00Z",
      slug = "test-article"
    ),
    ArticleInfo(
      authorThumbnail = null,
      authorUsername = "anotheruser",
      title = "Another Test Article",
      description = "Another Description",
      tags = listOf("test", "mvikotlin"),
      createdAt = "2023-01-02T12:00:00Z",
      slug = "another-test-article"
    )
  )

  @BeforeTest
  fun setUp() {
    articlesListService = mock()
    articlesListStore = ArticlesListStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      articlesListService,
      testDispatcher,
    ).create(defaultSearchFilter, autoInit = false)
  }

  @AfterTest
  fun reset() {
    articlesListStore.dispose()
  }

  @Test
  fun testInitialStateIsLoading() = runTest(testDispatcher) {
    // the initial state should be Loading
    assertTrue(articlesListStore.state is ArticlesListState.Loading)
  }

  @Test
  fun testLoadArticlesSuccess() = runTest(testDispatcher) {
    val stateChannel = Channel<ArticlesListState>()

    // Given
    everySuspend { 
      articlesListService.getInitialArticles(defaultSearchFilter) 
    } returns testArticles

    // When
    val disposable = articlesListStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    articlesListStore.init()

    // Then
    stateChannel.receive() // Ignore initial Loading state
    val loadedState = stateChannel.receive() as ArticlesListState.Loaded
    
    // Verify state transitions
    assertEquals(testArticles, loadedState.collectedThumbInfos)
    assertEquals(false, loadedState.isLoadingMore)
    
    // Verify service called correctly
    verifySuspend(exactly(1)) { articlesListService.getInitialArticles(defaultSearchFilter) }

    disposable.dispose()
  }

  @Test
  fun testLoadArticlesFailure() = runTest(testDispatcher) {
    val testScope = TestScope(testDispatcher)
    val labelChannel = articlesListStore.labelsChannel(testScope)

    // Given
    val errorMessage = "Failed to load articles"
    everySuspend { 
      articlesListService.getInitialArticles(any()) 
    } throws RuntimeException(errorMessage)

    // When - init the store to trigger the loading of articles
    articlesListStore.init()

    // Then - should emit a failure label with the error message
    val label = labelChannel.receive() as ArticlesListLabel.Failure
    assertEquals(errorMessage, label.message)
    assertTrue(label.exception is RuntimeException)

    // Verify service called
    verifySuspend(exactly(1)) { articlesListService.getInitialArticles(any()) }
  }

}
