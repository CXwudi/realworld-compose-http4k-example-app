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
        if (newMsg.targetMode != state().mode) {
          log.info { "Switching main page mode to ${newMsg.targetMode}" }
          dispatch(newMsg)
        }
      }
      onIntent<MainNavIntent.ModeSwitching> { intent ->
        val newMode = intent.targetMode
        if (newMode != state().mode) {
          log.info { "Switching main page mode to $newMode" }
          dispatch(Msg.ModeSwitching(newMode))
        }
      }

      onIntent<MainNavIntent.MenuItemSwitching> { intent ->
        val newMenuItem = intent.targetMenuItem
        val currentState = state()
        val newIdx: Int = requireNotNull(currentState.indexOfMenuItem(newMenuItem)) {
          "$newMenuItem not found in $currentState, this should not happen"
        }
        if (currentState.pageIndex != newIdx) {
          log.info { "Switching to page at index $newIdx" }
          dispatch(Msg.MenuIndexSwitching(newIdx))
        }
      }
    }

  private val reducer = Reducer<MainNavState, Msg> { msg ->
    when (msg) {
      is Msg.ModeSwitching -> copy(mode = msg.targetMode, pageIndex = 0)
      is Msg.MenuIndexSwitching -> copy(pageIndex = msg.targetIndex)
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
    initialState = MainNavState(MainNavMode.NOT_LOGGED_IN, 0),
    bootstrapper = createBootstrapper(),
    executorFactory = executor,
    reducer = reducer,
  )

  private data class Action(
    val userConfigState: UserConfigState
  ) {
    fun toMsg() = when (userConfigState) {
      is UserConfigState.Loaded -> {
        if (userConfigState.token.isNullOrBlank()) {
          Msg.ModeSwitching(MainNavMode.NOT_LOGGED_IN)
        } else {
          Msg.ModeSwitching(MainNavMode.LOGGED_IN)
        }
      }

      is UserConfigState.Loading -> {
        error("Should not happen since the main page appears only after the user config is loaded")
      }
    }
  }

  private sealed interface Msg {
    data class ModeSwitching(val targetMode: MainNavMode): Msg
    data class MenuIndexSwitching(val targetIndex: Int): Msg
  }

}

private val log = KotlinLogging.logger { }