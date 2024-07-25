package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import org.lighthousegames.logging.logging
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LandingPageStoreTest {

  lateinit var userConfigService: UserConfigService
  lateinit var landingPageStore: Store<LandingPageIntent, LandingPageState, LandingPageToNextPageLabel>

  @BeforeTest
  fun setUp() {
    userConfigService = mock()
    landingPageStore = LandingPageStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigService,
      Dispatchers.Default
    ).createStore()
  }

  @AfterTest
  fun reset() {
    landingPageStore.dispose()
  }

  @Test
  fun testNormalFlow1() = runTest {
    // Can't make Store.labels Flow way working in single-threaded env like JS or single-threaded Dispatcher
    val channel = Channel<LandingPageToNextPageLabel>()
    val scope = CoroutineScope(coroutineContext)

    landingPageStore.labels(observer {
      scope.launch {
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

//  @Test
//  fun normalFlowStucked() = runTest {
//    // the labels Flow way will be stuck if running in single-threaded env like JS or single-threaded Dispatcher
//    // However, from debugging, the label is actually dispatched, but for some reason, we can't receive it
//    // highly suspected that
//    val job = launch {
//      landingPageStore.labels.collect {
//        log.d { "Received $it" }
//        assertEquals(LandingPageToNextPageLabel, it)
//        verifySuspend(exactly(1)) {
//          userConfigService.setUrl("a change")
//        }
//      }
//    }
//
//    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
//    assertEquals("a change", landingPageStore.state.url)
//    landingPageStore.accept(LandingPageIntent.ToNextPage)
//
//    job.join()
//  }


  @Test
  fun testErrorFlow1() = runTest {
    everySuspend { userConfigService.setUrl("") } throws IllegalArgumentException("some error")

    val channel = Channel<LandingPageState>()
    val scope = CoroutineScope(coroutineContext)
    landingPageStore.states(observer {
      scope.launch {
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