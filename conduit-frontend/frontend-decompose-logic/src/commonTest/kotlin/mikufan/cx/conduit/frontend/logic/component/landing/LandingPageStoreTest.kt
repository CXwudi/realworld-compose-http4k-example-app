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
import kotlinx.coroutines.Dispatchers
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
  }

  @Test
  fun testNormalFlow1() {
    landingPageStore.accept(LandingPageIntent.TextChanged("a change"))
    assertEquals("a change", landingPageStore.state.url)

    landingPageStore.accept(LandingPageIntent.ToNextPage)

    landingPageStore.labels(observer {
      assertEquals(LandingPageToNextPageLabel, it)
      verifySuspend(exactly(1)) {
        userConfigService.setUrl("a change")
      }
    })
  }

  @Test
  fun testErrorFlow1() {
    everySuspend { userConfigService.setUrl("") } throws IllegalArgumentException("some error")
    landingPageStore.accept(LandingPageIntent.ToNextPage)

    landingPageStore.states(observer {
      if (it.errorMsg.isNotBlank()) {
        assertEquals("some error", it.errorMsg)
        verifySuspend(exactly(1)) {
          userConfigService.setUrl("")
        }
      }
    })
  }
}

private val log = logging()