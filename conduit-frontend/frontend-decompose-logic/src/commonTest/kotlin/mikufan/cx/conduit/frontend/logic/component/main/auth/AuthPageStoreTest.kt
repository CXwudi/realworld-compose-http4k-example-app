package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthPageStoreTest {

  lateinit var userConfigService: UserConfigService
  lateinit var authPageStore: Store<AuthPageIntent, AuthPageState, Nothing>

  @BeforeTest
  fun setUp() {
    userConfigService = mock()
    authPageStore = AuthPageStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigService,
      Dispatchers.Default,
    ).createStore()
  }

  @AfterTest
  fun reset() {
    authPageStore.dispose()
  }

  @Test
  fun normalFlow1() {
    authPageStore.accept(AuthPageIntent.UsernameChanged("new username"))
    assertEquals(authPageStore.state.username, "new username")
    authPageStore.accept(AuthPageIntent.PasswordChanged("new password"))
    assertEquals(authPageStore.state.password, "new password")

    authPageStore.accept(AuthPageIntent.SwitchMode)
    assertEquals(authPageStore.state.mode, AuthPageMode.REGISTER)
  }

  @Test
  fun backToLanding() = runTest {
    authPageStore.accept(AuthPageIntent.BackToLanding)
    verifySuspend(exactly(1)) {
      userConfigService.reset()
    }
  }
}