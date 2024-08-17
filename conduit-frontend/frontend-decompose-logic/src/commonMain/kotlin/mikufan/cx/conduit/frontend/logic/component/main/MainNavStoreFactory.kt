package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.mvikotlin.core.store.Bootstrapper
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mikufan.cx.conduit.frontend.logic.component.util.createWithoutMsg
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState
import org.lighthousegames.logging.logging

class MainNavStoreFactory(
  private val storeFactory: StoreFactory,
  private val userConfigService: UserConfigService,
  private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {

  private val executor =
    coroutineExecutorFactory<MainNavIntent, Action, MainNavState, MainNavState, Nothing>(dispatcher) {
      onAction<Action> {
        val newState = it.toMainNavState()
        if (newState.mode != state().mode) {
          log.i { "Switching main page mode to ${newState.mode}" }
          dispatch(newState)
        }
      }
      onIntent<ModeSwitchingIntent> { intent ->
        val newMode = when (intent) {
          is MainNavIntent.ToSignInMode -> MainNavMode.LOGGED_IN
          is MainNavIntent.ToLogoutMode -> MainNavMode.NOT_LOGGED_IN
        }
        if (newMode != state().mode) {
          log.i { "Switching main page mode to $newMode" }
          dispatch(MainNavState(newMode, 0))
        }
      }

      onIntent<PageSwitchingIntent> { intent ->
        val newMenuItem = PageSwitchingIntent.pageSwitchingIntent2MenuItem(intent)
        val currentState = state()
        val newIdx: Int = requireNotNull(currentState.indexOfMenuItem(newMenuItem)) {
          "$newMenuItem not found in $currentState, this should not happen"
        }
        if (currentState.pageIndex != newIdx) {
          log.i { "Switching to page at index $newIdx" }
          dispatch(currentState.copy(pageIndex = newIdx))
        }
      }
    }

  private fun createBootstrapper(): Bootstrapper<Action> =
    coroutineBootstrapper(dispatcher) {
      launch {
        userConfigService.userConfigFlow.collect {
          dispatch(Action(it))
        }
      }
    }

  fun createStore() = storeFactory.createWithoutMsg(
    name = "MainNavStore",
    initialState = MainNavState(MainNavMode.NOT_LOGGED_IN, 0),
    bootstrapper = createBootstrapper(),
    executorFactory = executor,
  )

  private data class Action(
    val userConfigState: UserConfigState
  ) {
    fun toMainNavState() = when (userConfigState) {
      is UserConfigState.Loaded -> {
        if (userConfigState.token.isNullOrBlank()) {
          MainNavState(MainNavMode.NOT_LOGGED_IN, 0)
        } else {
          MainNavState(MainNavMode.LOGGED_IN, 0)
        }
      }

      is UserConfigState.Loading -> {
        error("Should not happen since the main page appears only after the user config is loaded")
      }
    }
  }

}

private val log = logging()