package mikufan.cx.conduit.frontend.logic.component.main.auth

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore

class AuthPageStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigKStore: UserConfigKStore,
  dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  private val executorFactory =
    coroutineExecutorFactory<AuthPageIntent, Nothing, AuthPageState, Msg, Unit>(dispatcher) {
      onIntent<AuthPageIntent.UsernameChanged> {
        dispatch(Msg.UsernameChanged(it.username))
      }
      onIntent<AuthPageIntent.PasswordChanged> {
        dispatch(Msg.PasswordChanged(it.password))
      }
      onIntent<AuthPageIntent.SwitchMode> {
        val state = state()
        dispatch(Msg.SwitchMode(state.mode.opposite))
      }
      onIntent<AuthPageIntent.AuthAction> {
        // TODO: do either register or login, both will return token in success case
      }

      onIntent<AuthPageIntent.BackToLanding> {
        launch {
          withContext(Dispatchers.Default) {
            userConfigKStore.reset()
          }
          publish(Unit) // purely for test purpose
        }
      }
    }

  private val reducer = Reducer<AuthPageState, Msg> { msg ->
    when (msg) {
      is Msg.UsernameChanged -> this.copy(username = msg.username)
      is Msg.PasswordChanged -> this.copy(password = msg.password)
      is Msg.SwitchMode -> this.copy(mode = msg.mode)
    }
  }

  fun createStore() = storeFactory.create(
    name = "AuthPageStore",
    initialState = AuthPageState("", "", AuthPageMode.SIGN_IN),
    executorFactory = executorFactory,
    reducer = reducer
  )

  private sealed interface Msg {
    data class UsernameChanged(val username: String) : Msg
    data class PasswordChanged(val password: String) : Msg
    data class SwitchMode(val mode: AuthPageMode) : Msg
  }

}