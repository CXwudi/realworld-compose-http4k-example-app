package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponentFactory
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

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
  val childStack: Value<ChildStack<*, MainNavComponentChild>>
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
  private val mainNavStoreFactory: MainNavStoreFactory,
  private val authPageComponentFactory: AuthPageComponentFactory,
) : MainNavComponent, ComponentContext by componentContext {

  private val store = instanceKeeper.getStore { mainNavStoreFactory.createStore() }

  override val state: StateFlow<MainNavState> = store.stateFlow(coroutineScope())

  override fun send(intent: MainNavIntent) = store.accept(intent)

  private val stackNavigation = StackNavigation<Config>()

  override val childStack: Value<ChildStack<*, MainNavComponentChild>> =
    childStack(
      source = stackNavigation,
      initialConfiguration = Config.MainFeed,
      serializer = Config.serializer(),
      childFactory = ::childFactory
    )

  init {
    coroutineScope().launch {
      setupUserConfigStateToNavigationMapping()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun setupUserConfigStateToNavigationMapping() {
    store.stateFlow
      .collect {
        log.debug { "Current state is $it" }
        stackNavigation.replaceCurrent(enumToConfig(it.currentMenuItem))
      }
  }

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MainNavComponentChild = when (config) {
    Config.MainFeed -> MainNavComponentChild.MainFeed
    Config.Favourite -> MainNavComponentChild.Favourite
    Config.Me -> MainNavComponentChild.Me
    Config.SignInUp -> MainNavComponentChild.SignInUp(
      authPageComponentFactory.create(componentContext)
    )
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

class MainNavComponentFactory(
  private val storeFactory: MainNavStoreFactory,
  private val authPageComponentFactory: AuthPageComponentFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultMainNavComponent(
    componentContext = componentContext,
    mainNavStoreFactory = storeFactory,
    authPageComponentFactory = authPageComponentFactory,
  )
}

private val log = KotlinLogging.logger { }