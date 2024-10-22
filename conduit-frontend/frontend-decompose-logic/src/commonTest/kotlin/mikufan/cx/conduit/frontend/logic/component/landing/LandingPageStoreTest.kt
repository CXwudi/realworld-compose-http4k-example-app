package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.labelsChannel
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LandingPageStoreTest {

  private val testDispatcher = StandardTestDispatcher()

  lateinit var userConfigKStore: UserConfigKStore
  lateinit var landingPageStore: Store<LandingPageIntent, LandingPageState, LandingPageToNextPageLabel>

  @BeforeTest
  fun setUp() {
    userConfigKStore = mock()
    landingPageStore = LandingPageStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigKStore,
      testDispatcher
    ).createStore()
  }

  @AfterTest
  fun reset() {
    landingPageStore.dispose()
  }

  @Test
  fun testNormalFlow1() = runTest(testDispatcher) {
    // Can't make Store.labels Flow way working in single-threaded env like JS or single-threaded Dispatcher
    val channel = Channel<LandingPageToNextPageLabel>()

    landingPageStore.labels(observer(
      onComplete = { channel.close() },
      onNext = {
        this.launch {
          channel.send(it)
          log.debug { "Received $it" }
        }
      }
    ))

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)

    val label = channel.receive()
    assertEquals(label, LandingPageToNextPageLabel)
    verifySuspend(exactly(1)) {
      userConfigKStore.setUrl("a change")
    }
  }

  @OptIn(ExperimentalMviKotlinApi::class)
  @Test
  fun testNormalFlowWithAnotherWay() = runTest(testDispatcher) {
    // the labelsChannel internally will create a coroutine and just wait for cancellation
    val separateScope = TestScope(testDispatcher) // hence using separate scope for label channel to unblock the runTest
    val labelsChannel = landingPageStore.labelsChannel(separateScope)

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)

    val label = labelsChannel.receive()
    log.debug { "Received label" }
    assertEquals(label, LandingPageToNextPageLabel)
    verifySuspend(exactly(1)) {
      userConfigKStore.setUrl("a change")
    }
    log.debug { "After verify" }
    separateScope.cancel()
  }

  @Test
  fun testNormalFlowWithFlow() = runTest(testDispatcher) {
    // the labels Flow works and the label is actually dispatched.
    // however, the launched coroutine is stuck on the collect call, so the test will never finish
    // if the store.dispose() is not called before runTest finishes
    launch {
      landingPageStore.labels.collect {
        log.debug { "Received $it" }
        assertEquals(LandingPageToNextPageLabel, it)
        verifySuspend(exactly(1)) {
          userConfigKStore.setUrl("a change")
        }
        log.debug { "Test finished successfully" }
      }
    }

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)
    landingPageStore.dispose()
  }

  @Test
  fun testErrorFlow1() = runTest(testDispatcher) {
    everySuspend { userConfigKStore.setUrl("") } throws IllegalArgumentException("some error")

    val channel = Channel<LandingPageState>()
    landingPageStore.states(observer {
      this.launch {
        channel.send(it)
        log.debug { "Received $it" }
      }
    })

    landingPageStore.accept(LandingPageIntent.ToNextPage)

    channel.receive() // ignore the first initial state
    val newState = channel.receive()

    assertEquals("some error", newState.errorMsg)
    verifySuspend(exactly(1)) {
      userConfigKStore.setUrl("")
    }
    channel.close()
  }
}

private val log = KotlinLogging.logger { }