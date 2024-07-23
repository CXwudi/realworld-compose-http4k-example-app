package mikufan.cx.conduit.frontend.logic.component.landing

import com.arkivanov.mvikotlin.core.rx.observer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import org.lighthousegames.logging.logging
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
      DefaultStoreFactory(),
      userConfigService
    ).createStore()
  }

  init {
    Dispatchers.setMain(Dispatchers.Unconfined)
  }

  @Test
  fun testNormalFlow1() = runTest {

    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)

    landingPageStore.accept(LandingPageIntent.ToNextPage)
    verifySuspend {
      userConfigService.setUrl("a change")
    }

    landingPageStore.labels(observer {
      assertEquals(LandingPageToNextPageLabel, it)
    })
  }

  @Test
  fun testErrorFlow1() = runTest {
    everySuspend { userConfigService.setUrl("") } throws IllegalArgumentException("some error")
    landingPageStore.accept(LandingPageIntent.ToNextPage)
    verifySuspend {
      userConfigService.setUrl("")
    }
    landingPageStore.states(observer {
      if (it.errorMsg.isNotBlank()) {
        assertEquals("some error", it.errorMsg)
      }
    })
  }
}

private val log = logging()