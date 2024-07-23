package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.asValue
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState

interface MainNavComponent {
  val childSlot: Value<ChildSlot<*, MainNavComponentChild>>
  val state: Value<MainNavState>
}

sealed interface MainNavComponentChild {

  // TODO: each class need a component class, e.g. LandingPageComponent
  data object MainFeed : MainNavComponentChild
  data object Favourite : MainNavComponentChild
  data object Me : MainNavComponentChild
  data object SignInUp : MainNavComponentChild

}

data class MainNavState(
  val menuItems: List<MainNavMenuItem>
)

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

  companion object {
    private val unloginMenuItems = listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.SignInUp
    )

    private val loginMenuItems = listOf(
      MainNavMenuItem.Feed,
      MainNavMenuItem.Favourite,
      MainNavMenuItem.Me
    )
  }

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

  override val state: Value<MainNavState> =
    userConfigService.userConfigStateFlow.map { userConfigState ->
      val listOfMenuItem = when (userConfigState) {
        is UserConfigState.Loaded -> {
          if (userConfigState.token.isNullOrBlank()) {
            unloginMenuItems
          } else {
            loginMenuItems
          }
        }

        is UserConfigState.Loading -> {
          error("Should not happen since the main page appears only after the user config is loaded")
        }
      }
      MainNavState(listOfMenuItem)
    }
      .distinctUntilChanged()
      .stateIn(
        coroutineScope(),
        SharingStarted.Eagerly,
        MainNavState(unloginMenuItems)
      ).asValue(coroutineScope())

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