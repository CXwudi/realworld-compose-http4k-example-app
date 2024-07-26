package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mikufan.cx.conduit.frontend.logic.component.util.createWithoutMsg
import mikufan.cx.conduit.frontend.logic.service.UserConfigService

class AuthPageStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigService: UserConfigService,
  dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  private val executorFactory =
    coroutineExecutorFactory<AuthPageIntent, Nothing, AuthPageState, AuthPageState, Nothing>(dispatcher) {
      onIntent<AuthPageIntent.UsernameChanged> {
        dispatch(state().copy(username = it.username))
      }
      onIntent<AuthPageIntent.PasswordChanged> {
        dispatch(state().copy(password = it.password))
      }
      onIntent<AuthPageIntent.SwitchMode> {
        val state = state()
        dispatch(state.copy(mode = state.mode.opposite))
      }
      onIntent<AuthPageIntent.AuthAction> {
        // TODO: do either register or login, both will return token in success case
      }

      onIntent<AuthPageIntent.BackToLanding> {
        launch {
          withContext(Dispatchers.Default) {
            userConfigService.reset()
          }
        }
      }
    }

  fun createStore() = storeFactory.createWithoutMsg(
    name = "AuthPageStore",
    initialState = AuthPageState("", "", AuthPageMode.SIGN_IN),
    executorFactory = executorFactory,
  )

}