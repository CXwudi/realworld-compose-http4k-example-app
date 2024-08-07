package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.DefaultAuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.util.LocalKoinComponent
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent
import org.koin.core.component.get
import org.lighthousegames.logging.logging

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
  data class SignInUp(val component: AuthPageComponent) : MainNavComponentChild

}

class DefaultMainNavComponent(
  componentContext: ComponentContext,
  private val koin: LocalKoinComponent,
  private val mainNavStoreFactory: MainNavStoreFactory
) : MainNavComponent, ComponentContext by componentContext {


  private val store = instanceKeeper.getStore { mainNavStoreFactory.createStore() }

  override val state: StateFlow<MainNavState> = store.stateFlow(coroutineScope())

  override fun send(intent: MainNavIntent) = store.accept(intent)

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
    Config.SignInUp -> MainNavComponentChild.SignInUp(koin.createAuthPageComponent(componentContext))
  }

  private fun LocalKoinComponent.createAuthPageComponent(componentContext: ComponentContext): AuthPageComponent =
    DefaultAuthPageComponent(
      componentContext = componentContext,
      koin = this,
      authPageStoreFactory = get()
    )

  init {
    coroutineScope().launch {
      setupStateValueToNavigationMapping()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun setupStateValueToNavigationMapping() {
    store.stateFlow
      .collect {
        log.d { "Current state is $it" }
        slotNavigation.activate(enumToConfig(it.currentMenuItem))
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

private val log = logging()