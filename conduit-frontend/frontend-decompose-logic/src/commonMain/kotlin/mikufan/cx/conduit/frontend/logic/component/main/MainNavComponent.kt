package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreateSimple
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent
import mikufan.cx.conduit.frontend.logic.component.util.asStateFlow
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState

/**
 * The component for the main page, it unfortunately breaks the MVI pattern where it has two state values.
 *
 * One from the Decompose navigation ([childSlot]) and another we create ourselves ([MainNavState]).
 *
 * The [send] method is divided into two different methods, one for navigation and one for state update.
 *
 * The [MainNavIntent] is also divided into two groups ([ModeSwitchingIntent] and [PageSwitchingIntent]), to
 * distinguish between intent for the navigation and the state update.
 */
interface MainNavComponent : MviComponent<MainNavIntent, MainNavState> {
  val childSlot: Value<ChildSlot<*, MainNavComponentChild>>
}

sealed interface MainNavComponentChild {

  // TODO: each class need a component class, e.g. LandingPageComponent
  data object MainFeed : MainNavComponentChild
  data object Favourite : MainNavComponentChild
  data object Me : MainNavComponentChild
  data object SignInUp : MainNavComponentChild

}

class DefaultMainNavComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  private val userConfigService: UserConfigService,
) : MainNavComponent, ComponentContext by componentContext {


  private val _state = instanceKeeper.getOrCreateSimple {
    MutableValue(MainNavState(MainNavMode.NOT_LOGGED_IN, 0))
  }

  override val state: Value<MainNavState> = _state

  private val slotNavigation = SlotNavigation<Config>()

  override val childSlot: Value<ChildSlot<*, MainNavComponentChild>> =
    childSlot(
      source = slotNavigation,
      initialConfiguration = { Config.MainFeed },
      serializer = Config.serializer(),
      childFactory = ::childFactory
    )

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MainNavComponentChild = when (config) {
    Config.MainFeed -> MainNavComponentChild.MainFeed
    Config.Favourite -> MainNavComponentChild.Favourite
    Config.Me -> MainNavComponentChild.Me
    Config.SignInUp -> MainNavComponentChild.SignInUp
  }

  init {
    coroutineScope().apply {
      launch { setupLoginStatusChange() }
      launch { setupStateValueToNavigationMapping() }
    }
  }

  private suspend fun setupLoginStatusChange() {
    userConfigService.userConfigFlow.map { userConfigState ->
      when (userConfigState) {
        is UserConfigState.Loaded -> {
          if (userConfigState.token.isNullOrBlank()) {
            MainNavIntent.ToLogoutMode
          } else {
            MainNavIntent.ToSignInMode
          }
        }

        is UserConfigState.Loading -> {
          error("Should not happen since the main page appears only after the user config is loaded")
        }
      }
    }
      .distinctUntilChanged()
      .collect {
        send(it)
      }
  }

  private suspend fun setupStateValueToNavigationMapping() {
    state.asStateFlow()
      .collect {
        slotNavigation.activate(enumToConfig(it.currentMenuItem))
      }
  }

  override fun send(intent: MainNavIntent) {
    when (intent) {
      is ModeSwitchingIntent -> handleModeSwitchingIntent(intent)
      is PageSwitchingIntent -> handlePageSwitchingIntent(intent)
    }
  }

  private fun handleModeSwitchingIntent(intent: ModeSwitchingIntent) {
    val newMode = when (intent) {
      is MainNavIntent.ToSignInMode -> MainNavMode.LOGGED_IN
      is MainNavIntent.ToLogoutMode -> MainNavMode.NOT_LOGGED_IN
    }
    if (newMode != state.value.mode) {
      _state.value = MainNavState(newMode, 0)
    }
  }

  private fun handlePageSwitchingIntent(intent: PageSwitchingIntent) {
    val newMenuItem = PageSwitchingIntent.pageSwitchingIntent2MenuItem(intent)
    val currentState = state.value
    val newIdx: Int = requireNotNull(currentState.indexOfMenuItem(newMenuItem)) {
      "$newMenuItem not found in $currentState, this should not happen"
    }
    if (currentState.pageIndex != newIdx) {
      _state.value = currentState.copy(pageIndex = newIdx)
    }
  }

  private fun enumToConfig(enum: MainNavMenuItem): Config = when (enum) {
    MainNavMenuItem.Feed -> Config.MainFeed
    MainNavMenuItem.Favourite -> Config.Favourite
    MainNavMenuItem.Me -> Config.Me
    MainNavMenuItem.SignInUp -> Config.SignInUp
  }

  @Serializable
  sealed interface Config {

    @Serializable
    data object MainFeed : Config

    @Serializable
    data object Favourite : Config

    @Serializable
    data object Me : Config

    @Serializable
    data object SignInUp : Config
  }


}