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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import org.lighthousegames.logging.logging
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LandingPageStoreTest {

  private val testDispatcher = StandardTestDispatcher()

  lateinit var userConfigService: UserConfigService
  lateinit var landingPageStore: Store<LandingPageIntent, LandingPageState, LandingPageToNextPageLabel>

  @BeforeTest
  fun setUp() {
    userConfigService = mock()
    landingPageStore = LandingPageStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigService,
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

    landingPageStore.labels(observer {
      this.launch {
        channel.send(it)
        log.d { "Received $it" }
      }
    })

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)

    val label = channel.receive()
    assertEquals(label, LandingPageToNextPageLabel)
    verifySuspend(exactly(1)) {
      userConfigService.setUrl("a change")
    }

    channel.close()
  }

  @OptIn(ExperimentalMviKotlinApi::class)
  @Test
  fun testNormalFlowWithAnotherWay() = runTest(testDispatcher) {
    val separateScope = TestScope(testDispatcher) // separate scope for label channel
    val labelsChannel = landingPageStore.labelsChannel(separateScope)

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)

    val label = labelsChannel.receive()
    log.d { "Received label" }
    assertEquals(label, LandingPageToNextPageLabel)
    verifySuspend(exactly(1)) {
      userConfigService.setUrl("a change")
    }
    log.d { "After verify" }
  }

  @Test
  fun testNormalFlowWithFlow() = runTest(testDispatcher) {
    // the labels Flow way will be stuck if running in single-threaded env like JS or single-threaded Dispatcher
    // However, from debugging, the label is actually dispatched, but for some reason, we can't receive it
    // highly suspected that
    launch {
      landingPageStore.labels.collect {
        log.d { "Received $it" }
        assertEquals(LandingPageToNextPageLabel, it)
        verifySuspend(exactly(1)) {
          userConfigService.setUrl("a change")
        }
        log.d { "Test finished successfully" }
      }
    }

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)
    landingPageStore.accept(LandingPageIntent.ToNextPage)
    landingPageStore.dispose()
  }

  @Test
  fun testErrorFlow1() = runTest(testDispatcher) {
    everySuspend { userConfigService.setUrl("") } throws IllegalArgumentException("some error")

    val channel = Channel<LandingPageState>()
    landingPageStore.states(observer {
      this.launch {
        channel.send(it)
        log.d { "Received $it" }
      }
    })

    landingPageStore.accept(LandingPageIntent.ToNextPage)

    channel.receive() // ignore the first initial state
    val newState = channel.receive()

    assertEquals("some error", newState.errorMsg)
    verifySuspend(exactly(1)) {
      userConfigService.setUrl("")
    }
    channel.close()
  }
}

private val log = logging()