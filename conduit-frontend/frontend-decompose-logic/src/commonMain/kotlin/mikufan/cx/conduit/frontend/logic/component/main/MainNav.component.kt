package mikufan.cx.conduit.frontend.logic.component.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.extensions.coroutines.states
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponent
import mikufan.cx.conduit.frontend.logic.component.main.auth.AuthPageComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.me.MeNavComponentFactory
import mikufan.cx.conduit.frontend.logic.component.util.MviComponent

/**
 * The component for the main page, it unfortunately breaks the MVI pattern where it has two state values.
 *
 * One from the Decompose navigation ([childStack]) and another we create ourselves ([MainNavState]).
 *
 */
interface MainNavComponent : MviComponent<MainNavIntent, MainNavState> {
  val childStack: Value<ChildStack<*, MainNavComponentChild>>
}

sealed interface MainNavComponentChild {

  // TODO: each class need a component class, e.g. LandingPageComponent
  data object MainFeed : MainNavComponentChild
  data object Favourite : MainNavComponentChild
  data class Me(val component: MeNavComponent) : MainNavComponentChild
  data class SignInUp(val component: AuthPageComponent) : MainNavComponentChild

}

class DefaultMainNavComponent(
  componentContext: ComponentContext,
  private val mainNavStoreFactory: MainNavStoreFactory,
  private val authPageComponentFactory: AuthPageComponentFactory,
  private val meNavComponentFactory: MeNavComponentFactory,
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

  private suspend fun setupUserConfigStateToNavigationMapping() {
    store.states
      .collectLatest {
        log.debug { "Switching to $it" }
        stackNavigation.replaceCurrent(enumToConfig(it.currentMenuItem))
      }
  }

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): MainNavComponentChild = when (config) {
    Config.MainFeed -> MainNavComponentChild.MainFeed
    Config.Favourite -> MainNavComponentChild.Favourite
    Config.Me -> MainNavComponentChild.Me(
      meNavComponentFactory.create(componentContext)
    )
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
  private val meNavComponentFactory: MeNavComponentFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultMainNavComponent(
    componentContext = componentContext,
    mainNavStoreFactory = storeFactory,
    authPageComponentFactory = authPageComponentFactory,
    meNavComponentFactory = meNavComponentFactory,
  )
}

private val log = KotlinLogging.logger { }