package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigKStore
import mikufan.cx.conduit.frontend.logic.repo.kstore.UserConfigState

class MainNavStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigKStore: UserConfigKStore,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  private val executor =
    coroutineExecutorFactory<MainNavIntent, Action, MainNavState, Msg, Nothing>(dispatcher) {
      onAction<Action> {
        val newMsg = it.toMsg()
        if (newMsg.targetState != state()) {
          log.info { "Switching main page state to ${newMsg.targetState}" }
          dispatch(newMsg)
        }
      }
      onIntent<MainNavIntent.StateSwitching> { intent ->
        val newState = intent.targetState
        if (newState != state()) {
          log.info { "Switching main page state to $newState" }
          dispatch(Msg.StateSwitching(newState))
        }
      }

      onIntent<MainNavIntent.MenuItemSwitching> { intent ->
        val newMenuItem = intent.targetMenuItem
        val currentState = state()
        val newIdx: Int = requireNotNull(currentState.indexOfMenuItem(newMenuItem)) {
          "MenuItem $newMenuItem not found in state with items: ${currentState.menuItems}. Current pageIndex: ${currentState.pageIndex}"
        }
        if (currentState.pageIndex != newIdx) {
          log.info { "Switching to page at index $newIdx" }
          dispatch(Msg.MenuIndexSwitching(newIdx))
        }
      }
    }

  private val reducer = Reducer<MainNavState, Msg> { msg ->
    when (msg) {
      is Msg.StateSwitching -> msg.targetState
      is Msg.MenuIndexSwitching -> with(pageIndex = msg.targetIndex)
    }
  }

  private fun createBootstrapper(): Bootstrapper<Action> =
    coroutineBootstrapper(dispatcher) {
      launch {
        userConfigKStore.userConfigFlow.collect {
          dispatch(Action(it))
        }
      }
    }

  fun createStore() = storeFactory.create(
    name = "MainNavStore",
    initialState = MainNavState.notLoggedIn(),
    bootstrapper = createBootstrapper(),
    executorFactory = executor,
    reducer = reducer,
  )

  private data class Action(
    val userConfigState: UserConfigState
  ) {
    fun toMsg() = when (userConfigState) {
      is UserConfigState.Landing -> Msg.StateSwitching(MainNavState.notLoggedIn())
      is UserConfigState.OnUrl -> Msg.StateSwitching(MainNavState.notLoggedIn())
      is UserConfigState.OnLogin -> Msg.StateSwitching(MainNavState.loggedIn(userConfigState.userInfo.username))
    }
  }

  private sealed interface Msg {
    data class StateSwitching(val targetState: MainNavState): Msg
    data class MenuIndexSwitching(val targetIndex: Int): Msg
  }

}

private val log = KotlinLogging.logger { }