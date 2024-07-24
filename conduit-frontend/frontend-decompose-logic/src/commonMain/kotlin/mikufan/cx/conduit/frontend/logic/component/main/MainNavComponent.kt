package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState

interface MainNavComponent {
  val childSlot: Value<ChildSlot<*, MainNavComponentChild>>
  val state: Value<MainNavState>

  fun send(intent: MainNavIntent)
}

sealed interface MainNavComponentChild {

  // TODO: each class need a component class, e.g. LandingPageComponent
  data object MainFeed : MainNavComponentChild
  data object Favourite : MainNavComponentChild
  data object Me : MainNavComponentChild
  data object SignInUp : MainNavComponentChild

}

data class MainNavState(
  val mode: MainNavMode
)

enum class MainNavMode(
  val menuItems: List<MainNavMenuItem>,
) {
  NOT_LOGGED_IN(
    listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.SignInUp
    )
  ),
  LOGGED_IN(
    listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.Favourite,
      MainNavMenuItem.Me
    )
  ),
}

sealed interface MainNavIntent {

  data object ToSignInMode: MainNavIntent
  data object ToLogoutMode: MainNavIntent

  data object ToFeedPage: MainNavIntent
  data object ToFavouritePage: MainNavIntent
  data object ToMePage: MainNavIntent
  data object ToSignInUpPage: MainNavIntent
}

enum class MainNavMenuItem(
  val menuName: String,
) {
  Feed("Feeds"),
  Favourite("Favourites"),
  Me("Me"),
  SignInUp("Sign in/up"),
}

class DefaultMainNavComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  private val userConfigService: UserConfigService,
) : MainNavComponent, ComponentContext by componentContext {

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

  private val _state = MutableValue(MainNavState(MainNavMode.NOT_LOGGED_IN))
  override val state: Value<MainNavState> get() = _state

  init {
    setupLoginStatusChange()
  }

  private fun setupLoginStatusChange() = coroutineScope().launch {
    userConfigService.userConfigStateFlow.map { userConfigState ->
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

  override fun send(intent: MainNavIntent) {
    when(intent) {
      is MainNavIntent.ToSignInMode -> _state.value = MainNavState(MainNavMode.LOGGED_IN)
      is MainNavIntent.ToLogoutMode -> _state.value = MainNavState(MainNavMode.NOT_LOGGED_IN)
      else -> TODO()
    }
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