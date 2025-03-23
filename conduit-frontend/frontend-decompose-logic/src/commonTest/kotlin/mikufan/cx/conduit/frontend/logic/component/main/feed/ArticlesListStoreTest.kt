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
  fun testLoadInitialArticlesSuccess() = runTest(testDispatcher) {
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
  fun testLoadInitialArticlesFailure() = runTest(testDispatcher) {
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

  @Test
  fun testLoadMoreSuccess() = runTest(testDispatcher) {
    val stateChannel = Channel<ArticlesListState>()
    val moreArticles = listOf(
      ArticleInfo(
        authorThumbnail = "https://example.com/avatar3.png",
        authorUsername = "thirduser",
        title = "Third Article",
        description = "More test content",
        tags = listOf("testing"),
        createdAt = "2023-01-03T12:00:00Z",
        slug = "third-article"
      )
    )

    // Given - store already loaded with initial articles
    everySuspend {
      articlesListService.getInitialArticles(defaultSearchFilter)
    } returns testArticles

    everySuspend {
      articlesListService.getArticles(defaultSearchFilter, offset = testArticles.size)
    } returns moreArticles

    // When
    val disposable = articlesListStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    articlesListStore.init()

    // Wait for initial loading to complete
    stateChannel.receive() // Loading state
    stateChannel.receive() // Loaded state with initial articles

    // Send LoadMore intent
    articlesListStore.accept(ArticlesListIntent.LoadMore)

    // Then - should transition to loading more state
    val loadingMoreState = stateChannel.receive() as ArticlesListState.Loaded
    assertEquals(true, loadingMoreState.isLoadingMore)
    assertEquals(testArticles, loadingMoreState.collectedThumbInfos)

    // Then - should transition to loaded state with more articles
    val finalState = stateChannel.receive() as ArticlesListState.Loaded
    assertEquals(false, finalState.isLoadingMore)
    assertEquals(testArticles + moreArticles, finalState.collectedThumbInfos)

    // Verify service calls
    verifySuspend(exactly(1)) { articlesListService.getInitialArticles(defaultSearchFilter) }
    verifySuspend(exactly(1)) {
      articlesListService.getArticles(defaultSearchFilter, offset = testArticles.size)
    }

    disposable.dispose()
  }

  @Test
  fun testLoadMoreFailure() = runTest(testDispatcher) {
    val stateChannel = Channel<ArticlesListState>()
    val testScope = TestScope(testDispatcher)
    val labelChannel = articlesListStore.labelsChannel(testScope)

    // Given - store already loaded with initial articles
    everySuspend {
      articlesListService.getInitialArticles(defaultSearchFilter)
    } returns testArticles

    val errorMessage = "Failed to load more articles"
    everySuspend {
      articlesListService.getArticles(defaultSearchFilter, offset = testArticles.size)
    } throws RuntimeException(errorMessage)

    // When
    val disposable = articlesListStore.states(observer(onNext = { this.launch { stateChannel.send(it) } }))
    articlesListStore.init()

    // Then - Wait for initial loading to complete
    stateChannel.receive() // Loading state
    stateChannel.receive() // Loaded state with initial articles

    // When - Send LoadMore intent
    articlesListStore.accept(ArticlesListIntent.LoadMore)

    // Then - should transition to loading more state
    val loadingMoreState = stateChannel.receive() as ArticlesListState.Loaded
    assertEquals(true, loadingMoreState.isLoadingMore)

    // Then - should transition back to loaded state with original articles
    val finalState = stateChannel.receive() as ArticlesListState.Loaded
    assertEquals(false, finalState.isLoadingMore)
    assertEquals(testArticles, finalState.collectedThumbInfos)

    // Should emit a failure label
    val failureLabel = labelChannel.receive() as ArticlesListLabel.Failure
    assertEquals(errorMessage, failureLabel.message)

    // Verify service calls
    verifySuspend(exactly(1)) { articlesListService.getInitialArticles(defaultSearchFilter) }
    verifySuspend(exactly(1)) {
      articlesListService.getArticles(defaultSearchFilter, offset = testArticles.size)
    }

    disposable.dispose()
  }

}
