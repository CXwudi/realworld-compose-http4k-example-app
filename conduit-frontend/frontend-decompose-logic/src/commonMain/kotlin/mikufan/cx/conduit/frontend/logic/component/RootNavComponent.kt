package mikufan.cx.conduit.frontend.logic.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponent
import mikufan.cx.conduit.frontend.logic.component.landing.LandingPageComponentFactory
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponent
import mikufan.cx.conduit.frontend.logic.component.main.MainNavComponentFactory
import mikufan.cx.conduit.frontend.logic.service.UserConfigService
import mikufan.cx.conduit.frontend.logic.service.UserConfigState

interface RootNavComponent {
  val childStack: Value<ChildStack<*, RootComponentChild>>
}

sealed interface RootComponentChild {

  data object Loading : RootComponentChild
  data class LandingPage(
    val component: LandingPageComponent
  ) : RootComponentChild

  data class MainPage(
    val component: MainNavComponent
  ) : RootComponentChild

}

class DefaultRootNavComponent(
  componentContext: ComponentContext,
  private val userConfigService: UserConfigService,
  private val landingPageComponentFactory: LandingPageComponentFactory,
  private val mainNavComponentFactory: MainNavComponentFactory,
) : RootNavComponent, ComponentContext by componentContext {

  private val stackNavigation = StackNavigation<Config>()

  override val childStack: Value<ChildStack<*, RootComponentChild>> =
    childStack(
      source = stackNavigation,
      initialConfiguration = Config.Loading,
      serializer = Config.serializer(),
      childFactory = ::childFactory
    )

  init {
    coroutineScope().launch {
      setupUserConfigStateToNavigation()
    }
  }

  private suspend fun setupUserConfigStateToNavigation() {
    userConfigService.userConfigFlow
      .map {
        when (it) {
          is UserConfigState.Loading -> Config.Loading
          is UserConfigState.Loaded -> {
            if (it.url.isNullOrBlank()) {
              Config.LandingPage
            } else {
              Config.MainPage
            }
          }
        }
      }
      .distinctUntilChanged()
      .collect {
        stackNavigation.replaceCurrent(it)
      }
  }

  private fun childFactory(
    config: Config,
    componentContext: ComponentContext
  ): RootComponentChild {
    val child = when (config) {
      Config.Loading -> RootComponentChild.Loading
      Config.LandingPage -> RootComponentChild.LandingPage(
        component = landingPageComponentFactory.create(componentContext)
      )

      Config.MainPage -> RootComponentChild.MainPage(
        component = mainNavComponentFactory.create(componentContext)
      )
    }
    return child
  }

  /**
   * This config doesn't need to contain any data,
   * because each page is pretty much fetching the data from services,
   *
   * E.g. Landing page pretty much fetch data from [UserConfigService], and
   * Main page will also fetch data mainly from various services.
   */
  @Serializable
  sealed interface Config {
    @Serializable
    data object Loading : Config

    @Serializable
    data object LandingPage : Config

    @Serializable
    data object MainPage : Config
  }
}

class RootNavComponentFactory(
  private val userConfigService: UserConfigService,
  private val landingPageComponentFactory: LandingPageComponentFactory,
  private val mainNavComponentFactory: MainNavComponentFactory,
) {
  fun create(componentContext: ComponentContext) = DefaultRootNavComponent(
    componentContext = componentContext,
    userConfigService = userConfigService,
    landingPageComponentFactory = landingPageComponentFactory,
    mainNavComponentFactory = mainNavComponentFactory,
  )
}
