package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.labelsChannel
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode.Companion.exactly
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthPageStoreTest {
  private val testDispatcher = StandardTestDispatcher()

  lateinit var userConfigKStore: UserConfigKStore
  lateinit var authPageStore: Store<AuthPageIntent, AuthPageState, Unit>

  @BeforeTest
  fun setUp() {
    userConfigKStore = mock()
    authPageStore = AuthPageStoreFactory(
      LoggingStoreFactory(DefaultStoreFactory()),
      userConfigKStore,
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

  @OptIn(ExperimentalMviKotlinApi::class)
  @Test
  fun backToLanding() = runTest(testDispatcher) {
    val channel = authPageStore.labelsChannel(TestScope()) // label channel in another scope

    authPageStore.accept(AuthPageIntent.BackToLanding)

    assertEquals(Unit, channel.receive())
    verifySuspend(exactly(1)) { userConfigKStore.reset() }
  }
}